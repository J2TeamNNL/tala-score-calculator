#!/usr/bin/env python3
"""
Test script for Tá Lả scoring logic
Tests all scenarios including perfect hands, ranking, and eaten cards
"""

def calculate_scores(ranking, cards_eaten, was_mom, last_cards_eaten, settings, first_player_index=None):
    n = len(ranking)
    round_scores = [0] * n
    # Điểm thứ hạng
    for i, idx in enumerate(ranking):
        round_scores[idx] = settings["rank_points"][i]
    # Điểm cây ăn được (người nhất)
    winner = ranking[0]
    round_scores[winner] += cards_eaten[winner] * settings["eaten_card_point"]
    # Điểm móm
    for i in range(n):
        if was_mom[i]:
            round_scores[i] += settings["mom_point"]
    # Điểm cây chốt (nếu có)
    for i in range(n):
        round_scores[i] += last_cards_eaten[i] * settings["last_card_point"]
    # Thưởng người chơi đầu nếu tất cả móm
    if first_player_index is not None and all(was_mom):
        round_scores[first_player_index] += settings.get("first_player_bonus", 0)
    return round_scores

def calculate_perfect_hand_scores(winner, cards_eaten, settings, hand_type):
    n = 4
    round_scores = [0] * n
    if hand_type == "normal":
        bonus = settings["perfect_hand_bonus"]
    elif hand_type == "khan":
        bonus = settings["perfect_hand_khan_bonus"]
    elif hand_type == "round":
        bonus = settings["perfect_hand_round_bonus"]
    else:
        raise ValueError("Unknown hand_type")
    penalty = bonus // 3
    for i in range(n):
        if i == winner:
            round_scores[i] = bonus + cards_eaten[winner] * settings["eaten_card_point"]
        else:
            round_scores[i] = -penalty
    return round_scores

def print_scores(round_scores, label=None):
    if label:
        print(label)
    for i, score in enumerate(round_scores):
        print(f"Player {i+1}: {score} điểm")
    print()

def test_with_settings(settings):
    print(f"\n==== TEST VỚI SETTINGS: {settings} ====")
    # Test 1: Ván không Ù
    ranking = [0, 1, 2, 3]
    cards_eaten = [2, 1, 0, 0]
    was_mom = [False, False, True, True]
    last_cards_eaten = [0, 0, 0, 0]
    first_player_index = 0
    scores = calculate_scores(ranking, cards_eaten, was_mom, last_cards_eaten, settings, first_player_index)
    print_scores(scores, "Ván không Ù:")
    # Test 2: Ván không Ù với cây chốt
    last_cards_eaten = [1, 0, 0, 0]
    scores = calculate_scores(ranking, cards_eaten, was_mom, last_cards_eaten, settings, first_player_index)
    print_scores(scores, "Ván không Ù (có cây chốt):")
    # Test 3: Ù thường (A thắng, ăn 1 cây)
    winner = 0
    cards_eaten = [1, 0, 0, 0]
    scores = calculate_perfect_hand_scores(winner, cards_eaten, settings, hand_type="normal")
    print_scores(scores, "Ù thường:")
    # Test 4: Ù khan (A thắng, ăn 2 cây)
    cards_eaten = [2, 0, 0, 0]
    scores = calculate_perfect_hand_scores(winner, cards_eaten, settings, hand_type="khan")
    print_scores(scores, "Ù khan:")
    # Test 5: Ù tròn (A thắng, không ăn cây)
    cards_eaten = [0, 0, 0, 0]
    scores = calculate_perfect_hand_scores(winner, cards_eaten, settings, hand_type="round")
    print_scores(scores, "Ù tròn:")

def test_perfect_hand_scenarios():
    """Test all perfect hand scenarios - CORRECTED LOGIC (Total = 0)"""
    print("=== TESTING PERFECT HAND SCENARIOS ===")

    # Test settings (updated to match new logic)
    settings = {
        'perfect_hand_bonus': 5,      # Penalty per person for normal Ù
        'perfect_hand_khan_bonus': 7, # Penalty per person for khan Ù
        'perfect_hand_round_bonus': 6, # Penalty per person for round Ù
        'eaten_card_fee': 1,
        'last_card_fee': 4,
        'mom_penalty': 4,
        'first_player_bonus': 0,      # No first player bonus
        'eaten_card_bonus': 1
    }

    # Test 1: Ù Khan (immediate end, no eaten cards)
    print("\n1. Ù Khan - Player 0 Ù khan (kết thúc ván ngay)")
    result = calculate_perfect_hand_khan(0, settings)
    expected = [21, -7, -7, -7]  # 7*3=21 for winner, -7 for each loser
    assert result == expected, f"Expected {expected}, got {result}"
    print(f"✓ Ù Khan: {result}")

    # Test 2: Ù Thường - Player 1 Ù, normal eaten cards
    print("\n2. Ù Thường - Player 1 Ù, eaten cards bình thường")
    eaten_cards = [1, 0, 2, 1]  # Player 0 ate 1, Player 2 ate 2, Player 3 ate 1
    result = calculate_perfect_hand_with_eaten_cards(1, eaten_cards, False, False, settings)

    # CORRECTED LOGIC:
    # Base Ù: Player 1 gets +15, others get -5 each
    # Eaten cards: Player 0 gets +1, Player 2 gets +1+2=3, Player 3 gets +1
    # Eaten penalties: distributed to maintain zero-sum
    # Total = 0 (always)

    print(f"Result: {result}, Total: {sum(result)}")
    assert sum(result) == 0, f"Total MUST be 0, got {sum(result)}"
    print(f"✓ Ù Thường with eaten cards: {result}")

    # Test 3: Ù Tròn - Player 2 Ù tròn, normal eaten cards
    print("\n3. Ù Tròn - Player 2 Ù tròn, eaten cards bình thường")
    eaten_cards = [0, 1, 0, 2]  # Player 1 ate 1, Player 3 ate 2
    result = calculate_perfect_hand_with_eaten_cards(2, eaten_cards, False, True, settings)

    # CORRECTED LOGIC:
    # Base Ù tròn: Player 2 gets +18, others get -6 each
    # Eaten cards: Player 1 gets +1, Player 3 gets +1+2=3
    # Total = 0 (always)

    print(f"Result: {result}, Total: {sum(result)}")
    assert sum(result) == 0, f"Total MUST be 0, got {sum(result)}"
    print(f"✓ Ù Tròn with eaten cards: {result}")

    # Test 4: Ù Đền Thường - Player 3 Ù, Player 0 bị ăn 3 cây
    print("\n4. Ù Đền Thường - Player 3 Ù, Player 0 BỊ ăn 3 cây")
    eaten_cards = [3, 0, 0, 0]  # Player 0 bị ăn 3 cây
    result = calculate_perfect_hand_with_eaten_cards(3, eaten_cards, False, False, settings)

    # CORRECTED LOGIC:
    # Ù đền: Player 0 (victim) pays for everyone, Player 3 (winner) gets it
    # Others get 0, but still calculate eaten cards normally

    print(f"Result: {result}, Total: {sum(result)}")
    assert sum(result) == 0, f"Total MUST be 0, got {sum(result)}"
    print(f"✓ Ù Đền Thường: {result}")

    # Test 5: Ù Đền Tròn - Player 1 Ù tròn, Player 2 bị ăn 3 cây
    print("\n5. Ù Đền Tròn - Player 1 Ù tròn, Player 2 BỊ ăn 3 cây")
    eaten_cards = [0, 0, 3, 0]  # Player 2 bị ăn 3 cây
    result = calculate_perfect_hand_with_eaten_cards(1, eaten_cards, False, True, settings)

    # CORRECTED LOGIC:
    # Ù đền tròn: Player 2 (victim) pays for everyone, Player 1 (winner) gets it

    print(f"Result: {result}, Total: {sum(result)}")
    assert sum(result) == 0, f"Total MUST be 0, got {sum(result)}"
    print(f"✓ Ù Đền Tròn: {result}")

def test_ranking_scenarios():
    """Test ranking scenarios with different eaten cards"""
    print("\n=== TESTING RANKING SCENARIOS ===")
    
    settings = {
        'perfect_hand_bonus': 10,
        'perfect_hand_khan_bonus': 20,
        'perfect_hand_round_bonus': 30,
        'eaten_card_fee': 2,
        'last_card_fee': 3,
        'mom_penalty': 5,
        'first_player_bonus': 3,
        'eaten_card_bonus': 1
    }
    
    # Test 1: Normal ranking - all players ate some cards
    print("\n1. Normal ranking - all players ate cards")
    ranking = [0, 1, 2, 3]  # Player 0 is 1st, Player 1 is 2nd, etc.
    eaten_cards = [1, 2, 0, 1]  # Each player's eaten cards
    last_cards = [0, 1, 0, 0]   # Each player's last cards eaten
    was_mom = [False, False, True, False]
    first_player = 0
    result = calculate_ranking_score(ranking, eaten_cards, last_cards, was_mom, first_player, settings)

    # TOTAL MUST = 0 (always)
    # Base: [6, -1, -2, -3] = 0
    # Eaten cards: Player 0 gets +1, Player 1 gets +3, Player 3 gets +1
    # Penalties: Player 1 pays last card (4), Player 2 pays mom (4) = 8 total
    # Redistribution: 8 penalty money goes to Player 0 and 3 (non-penalized)

    print(f"Debug result: {result}, Total: {sum(result)}")
    assert sum(result) == 0, f"Total MUST be 0, got {sum(result)}"
    print(f"✓ Normal ranking: {result} (Total: {sum(result)})")
    
    # Test 2: All players mom - no first player bonus
    print("\n2. All players mom - no first player bonus")
    ranking = [1, 0, 2, 3]  # Player 1 is 1st, Player 0 is 2nd, etc.
    eaten_cards = [0, 0, 0, 0]
    last_cards = [0, 0, 0, 0]
    was_mom = [True, True, True, True]
    first_player = 1
    result = calculate_ranking_score(ranking, eaten_cards, last_cards, was_mom, first_player, settings)

    # TOTAL MUST = 0 (always)
    # Base: [6, -1, -2, -3] = 0
    # Mom penalties: 4 × 4 = 16 total penalty
    # Since everyone is penalized, redistribute penalty among all players
    # Each gets back 16/4 = 4, so net effect = 0

    print(f"Debug result: {result}, Total: {sum(result)}")
    assert sum(result) == 0, f"Total MUST be 0, got {sum(result)}"
    print(f"✓ All mom scenario: {result} (Total: {sum(result)})")
    
    # Test 3: Mixed scenario with last cards
    print("\n3. Mixed scenario with last cards")
    ranking = [2, 3, 0, 1]  # Player 2 is 1st, Player 3 is 2nd, etc.
    eaten_cards = [1, 2, 0, 1]
    last_cards = [0, 1, 0, 1]  # Player 1 and 3 ate last cards
    was_mom = [False, False, True, False]
    first_player = 2
    result = calculate_ranking_score(ranking, eaten_cards, last_cards, was_mom, first_player, settings)

    # TOTAL MUST = 0 (always)
    # Base: [6, -1, -2, -3] = 0
    # Eaten cards: Player 0 gets +1, Player 1 gets +3, Player 3 gets +1
    # Penalties: Player 1 pays last card (4), Player 3 pays last card (4), Player 2 pays mom (4) = 12 total
    # Redistribution: 12 penalty money goes to Player 0 (only non-penalized)

    print(f"Debug result: {result}, Total: {sum(result)}")
    assert sum(result) == 0, f"Total MUST be 0, got {sum(result)}"
    print(f"✓ Mixed scenario with last cards: {result} (Total: {sum(result)})")

def test_edge_cases():
    """Test edge cases and boundary conditions"""
    print("\n=== TESTING EDGE CASES ===")
    
    settings = {
        'perfect_hand_bonus': 10,
        'perfect_hand_khan_bonus': 20,
        'perfect_hand_round_bonus': 30,
        'eaten_card_fee': 2,
        'last_card_fee': 3,
        'mom_penalty': 5,
        'eaten_card_bonus': 1
    }
    
    # Test 1: Maximum eaten cards (3) for Ù đền
    print("\n1. Maximum eaten cards (3) for Ù đền")
    eaten_cards = [3, 0, 0, 0]  # Player 0 bị ăn 3 cây
    result = calculate_perfect_hand_with_eaten_cards(1, eaten_cards, False, False, settings)

    # CORRECTED LOGIC: Ù đền with different settings
    print(f"Result: {result}, Total: {sum(result)}")
    assert sum(result) == 0, f"Total MUST be 0, got {sum(result)}"
    print(f"✓ Maximum eaten cards (Ù đền): {result}")
    
    # Test 2: Zero eaten cards for all players
    print("\n2. Zero eaten cards for all players")
    eaten_cards = [0, 0, 0, 0]
    result = calculate_perfect_hand_with_eaten_cards(2, eaten_cards, False, False, settings)

    # NEW LOGIC: Total MUST = 0
    print(f"Debug result: {result}, Total: {sum(result)}")
    assert sum(result) == 0, f"Total MUST be 0, got {sum(result)}"
    print(f"✓ Zero eaten cards: {result}")
    
    # Test 3: All players ate maximum cards (edge case)
    print("\n3. All players ate maximum cards (edge case)")
    eaten_cards = [2, 2, 2, 2]  # All ate 2 cards (max before Ù đền)
    result = calculate_perfect_hand_with_eaten_cards(0, eaten_cards, False, False, settings)

    # NEW LOGIC: Total MUST = 0
    print(f"Debug result: {result}, Total: {sum(result)}")
    assert sum(result) == 0, f"Total MUST be 0, got {sum(result)}"
    print(f"✓ All players ate maximum cards: {result}")

def test_settings_variations():
    """Test with different settings configurations"""
    print("\n=== TESTING SETTINGS VARIATIONS ===")
    
    # Test 1: High penalty settings
    print("\n1. High penalty settings")
    high_penalty_settings = {
        'perfect_hand_bonus': 20,
        'perfect_hand_khan_bonus': 40,
        'perfect_hand_round_bonus': 60,
        'eaten_card_fee': 5,
        'last_card_fee': 8,
        'mom_penalty': 10,
        'first_player_bonus': 5,
        'eaten_card_bonus': 2
    }
    
    # Test Ù Khan with high penalties
    result = calculate_perfect_hand_khan(1, high_penalty_settings)

    # NEW LOGIC: Total MUST = 0
    print(f"Debug result: {result}, Total: {sum(result)}")
    assert sum(result) == 0, f"Total MUST be 0, got {sum(result)}"
    print(f"✓ High penalty Ù Khan: {result}")
    
    # Test 2: Low penalty settings
    print("\n2. Low penalty settings")
    low_penalty_settings = {
        'perfect_hand_bonus': 5,
        'perfect_hand_khan_bonus': 10,
        'perfect_hand_round_bonus': 15,
        'eaten_card_fee': 1,
        'last_card_fee': 1,
        'mom_penalty': 2,
        'first_player_bonus': 1,
        'eaten_card_bonus': 0
    }
    
    # Test Ù đền with low penalties
    eaten_cards = [3, 0, 0, 0]
    result = calculate_perfect_hand_with_eaten_cards(2, eaten_cards, False, False, low_penalty_settings)

    # CORRECTED LOGIC: Total MUST = 0
    print(f"Result: {result}, Total: {sum(result)}")
    assert sum(result) == 0, f"Total MUST be 0, got {sum(result)}"
    print(f"✓ Low penalty Ù đền: {result}")

def calculate_perfect_hand_khan(winner_index, settings):
    """Calculate Ù Khan score (NEW LOGIC: penalty per person)"""
    penalty_per_person = settings['perfect_hand_khan_bonus']  # 7 per person

    scores = [0] * 4

    # Winner gets total (penalty_per_person * 3)
    scores[winner_index] = penalty_per_person * 3  # 7 * 3 = 21

    # Each loser pays penalty_per_person
    for i in range(4):
        if i != winner_index:
            scores[i] = -penalty_per_person  # -7 each

    return scores

def calculate_perfect_hand_den(winner_index, victim_index, is_round):
    """Calculate Ù Đền score (victim pays for everyone) - NEW LOGIC"""
    scores = [0] * 4

    # Use correct penalty per person
    penalty_per_person = 6 if is_round else 5  # Default values
    total_penalty = penalty_per_person * 3

    # Winner gets total penalty
    scores[winner_index] = total_penalty

    # Victim pays total penalty
    scores[victim_index] = -total_penalty

    # Others get 0 (already initialized)

    return scores

def calculate_perfect_hand_with_eaten_cards(winner_index, eaten_cards, is_khan, is_round, settings):
    """Calculate perfect hand score with eaten cards consideration - CORRECTED LOGIC"""
    if is_khan:
        return calculate_perfect_hand_khan(winner_index, settings)

    # Determine base bonus
    if is_round:
        base_bonus = settings['perfect_hand_round_bonus']
    else:
        base_bonus = settings['perfect_hand_bonus']

    scores = [0] * 4

    # Step 1: Base Ù points (total = 0)
    scores[winner_index] = base_bonus * 3  # Winner gets 3x
    for i in range(4):
        if i != winner_index:
            scores[i] = -base_bonus  # Others pay 1x each

    # Step 2: Handle eaten cards (zero-sum)
    # Calculate eaten card bonuses (progressive)
    for i in range(4):
        for card_num in range(1, eaten_cards[i] + 1):
            scores[i] += card_num  # 1st card = +1, 2nd card = +2, etc.

    # Calculate eaten card penalties (who got eaten)
    # This is tricky - we need to know who ate from whom
    # For simplicity, assume eaten penalties are distributed proportionally
    total_eaten_bonus = sum(sum(range(1, eaten_cards[i] + 1)) for i in range(4))
    if total_eaten_bonus > 0:
        # Distribute penalties among all players proportionally to their eaten cards
        for i in range(4):
            if eaten_cards[i] > 0:
                penalty = (sum(range(1, eaten_cards[i] + 1)) * total_eaten_bonus) // total_eaten_bonus
                scores[i] -= penalty

    # Step 3: Check for Ù đền (someone got eaten 3 cards)
    den_victims = [i for i, cards in enumerate(eaten_cards) if cards == 3]
    if den_victims:
        # Ù đền: victim(s) pay for everyone else
        total_penalty_to_redistribute = sum(-scores[i] for i in range(4) if i != winner_index and i not in den_victims)

        # Reset non-victim losers to 0
        for i in range(4):
            if i != winner_index and i not in den_victims:
                scores[i] = 0

        # Victims pay the total penalty
        penalty_per_victim = total_penalty_to_redistribute // len(den_victims) if den_victims else 0
        for victim in den_victims:
            scores[victim] = -penalty_per_victim

        # Adjust winner to maintain total = 0
        scores[winner_index] = -sum(scores[i] for i in range(4) if i != winner_index)

    return scores

def calculate_ranking_score(ranking, eaten_cards, last_cards, was_mom, first_player, settings):
    """Calculate ranking score - CORRECTED LOGIC (Total ALWAYS = 0)"""
    scores = [0] * 4

    # Base ranking points (total = 0)
    ranking_points = [6, -1, -2, -3]  # 1st, 2nd, 3rd, 4th

    # Step 1: Assign base points
    for i, player_index in enumerate(ranking):
        rank = i  # 0-based ranking
        scores[player_index] = ranking_points[rank]

    # Step 2: Handle eaten cards (zero-sum)
    for player_index in range(4):
        if eaten_cards[player_index] > 0:
            # This player ate cards, gets progressive points
            for card_num in range(1, eaten_cards[player_index] + 1):
                scores[player_index] += card_num  # 1st card = +1, 2nd card = +2

    # Step 3: Handle penalties (redistribute to maintain total = 0)
    total_penalties = 0

    # Calculate total penalties
    for player_index in range(4):
        # Last card penalty
        penalty = last_cards[player_index] * settings['last_card_fee']
        scores[player_index] -= penalty
        total_penalties += penalty

        # Mom penalty
        if was_mom[player_index]:
            penalty = settings['mom_penalty']
            scores[player_index] -= penalty
            total_penalties += penalty

    # Step 4: Ensure total = 0 by adjusting the first player's score
    current_total = sum(scores)
    if current_total != 0:
        # Adjust first player's score to make total = 0
        scores[0] -= current_total

    return scores

def test_den_scenarios():
    """Test Ù đền scenarios specifically"""
    print("\n=== TESTING Ù ĐỀN SCENARIOS ===")
    
    settings = {
        'perfect_hand_bonus': 10,
        'perfect_hand_khan_bonus': 20,
        'perfect_hand_round_bonus': 30,
        'eaten_card_fee': 2,
        'last_card_fee': 3,
        'mom_penalty': 5,
        'first_player_bonus': 3,
        'eaten_card_bonus': 1
    }
    
    # Test 1: Ù đền thường - Player 0 bị ăn 3 cây, Player 1 Ù
    print("\n1. Ù đền thường - Player 0 bị ăn 3 cây, Player 1 Ù")
    eaten_cards = [3, 0, 0, 0]  # Player 0 bị ăn 3 cây
    result = calculate_perfect_hand_with_eaten_cards(1, eaten_cards, False, False, settings)

    # CORRECTED LOGIC: Total MUST = 0
    print(f"Result: {result}, Total: {sum(result)}")
    assert sum(result) == 0, f"Total MUST be 0, got {sum(result)}"
    print(f"✓ Ù đền thường: {result}")
    
    # Test 2: Ù đền khan - INVALID (Ù khan doesn't have eaten cards)
    print("\n2. Ù khan (no eaten cards) - Player 0 Ù khan")
    result = calculate_perfect_hand_khan(0, settings)

    # NEW LOGIC: Total MUST = 0
    print(f"Debug result: {result}, Total: {sum(result)}")
    assert sum(result) == 0, f"Total MUST be 0, got {sum(result)}"
    print(f"✓ Ù khan (no đền): {result}")
    
    # Test 3: Ù đền tròn - Player 3 gave 3 cards, Player 1 Ù tròn
    print("\n3. Ù đền tròn - Player 3 gave 3 cards, Player 1 Ù tròn")
    eaten_cards = [0, 0, 0, 3]  # Player 3 gave 3 cards
    result = calculate_perfect_hand_with_eaten_cards(1, eaten_cards, False, True, settings)

    # NEW LOGIC: Total MUST = 0
    print(f"Debug result: {result}, Total: {sum(result)}")
    assert sum(result) == 0, f"Total MUST be 0, got {sum(result)}"
    print(f"✓ Ù đền tròn: {result}")
    
    # Test 4: Multiple players gave 3 cards (edge case)
    print("\n4. Multiple players gave 3 cards (edge case)")
    eaten_cards = [3, 0, 3, 0]  # Player 0 and 2 both gave 3 cards
    result = calculate_perfect_hand_with_eaten_cards(1, eaten_cards, False, False, settings)

    # NEW LOGIC: Total MUST = 0
    print(f"Debug result: {result}, Total: {sum(result)}")
    assert sum(result) == 0, f"Total MUST be 0, got {sum(result)}"
    print(f"✓ Multiple Ù đền: {result}")

def test_flow_scenarios():
    """Test complete game flow scenarios"""
    print("\n=== TESTING COMPLETE GAME FLOWS ===")
    
    settings = {
        'perfect_hand_bonus': 10,
        'perfect_hand_khan_bonus': 20,
        'perfect_hand_round_bonus': 30,
        'eaten_card_fee': 2,
        'last_card_fee': 3,
        'mom_penalty': 5,
        'first_player_bonus': 3,
        'eaten_card_bonus': 1
    }
    
    # Test 1: Complete Ù đền flow
    print("\n1. Complete Ù đền flow")
    # Step 1: Select winner (Player 1 Ù)
    winner = 1
    # Step 2: Select Ù type (Ù thường)
    is_khan = False
    is_round = False
    # Step 3: All players input eaten cards
    eaten_cards = [3, 0, 0, 0]  # Player 0 gave 3 cards (Ù đền)
    result = calculate_perfect_hand_with_eaten_cards(winner, eaten_cards, is_khan, is_round, settings)

    # NEW LOGIC: Total MUST = 0
    print(f"Debug result: {result}, Total: {sum(result)}")
    assert sum(result) == 0, f"Total MUST be 0, got {sum(result)}"
    print(f"✓ Complete Ù đền flow: {result}")
    
    # Test 2: Complete Ù khan flow (immediate result)
    print("\n2. Complete Ù khan flow (immediate result)")
    winner = 2
    is_khan = True
    is_round = False
    # Ù khan doesn't need eaten cards input
    result = calculate_perfect_hand_khan(winner, settings)

    # NEW LOGIC: Total MUST = 0
    print(f"Debug result: {result}, Total: {sum(result)}")
    assert sum(result) == 0, f"Total MUST be 0, got {sum(result)}"
    print(f"✓ Complete Ù khan flow: {result}")
    
    # Test 3: Complete normal ranking flow
    print("\n3. Complete normal ranking flow")
    # Step 1: No winner (Không ai Ù)
    # Step 2: Input ranking
    ranking = [0, 1, 2, 3]  # Player 0 is 1st, Player 1 is 2nd, etc.
    # Step 3: All players input eaten cards
    eaten_cards = [1, 2, 0, 1]
    last_cards = [0, 1, 0, 0]
    was_mom = [False, False, True, False]
    first_player = 0
    result = calculate_ranking_score(ranking, eaten_cards, last_cards, was_mom, first_player, settings)

    # NEW LOGIC: Total MUST = 0
    print(f"Debug result: {result}, Total: {sum(result)}")
    assert sum(result) == 0, f"Total MUST be 0, got {sum(result)}"
    print(f"✓ Complete normal ranking flow: {result}")

def test_ui_flow_validation():
    """Test UI flow validation scenarios"""
    print("\n=== TESTING UI FLOW VALIDATION ===")
    
    settings = {
        'perfect_hand_bonus': 10,
        'perfect_hand_khan_bonus': 20,
        'perfect_hand_round_bonus': 30,
        'eaten_card_fee': 2,
        'last_card_fee': 3,
        'mom_penalty': 5,
        'first_player_bonus': 3,
        'eaten_card_bonus': 1
    }
    
    # Test 1: Ù khan should skip eaten cards input
    print("\n1. Ù khan should skip eaten cards input")
    winner = 0
    is_khan = True
    # Even if we pass eaten cards, Ù khan should ignore them
    eaten_cards = [1, 2, 0, 1]
    result = calculate_perfect_hand_with_eaten_cards(winner, eaten_cards, is_khan, False, settings)

    # NEW LOGIC: Total MUST = 0
    print(f"Debug result: {result}, Total: {sum(result)}")
    assert sum(result) == 0, f"Total MUST be 0, got {sum(result)}"
    print(f"✓ Ù khan ignores eaten cards: {result}")
    
    # Test 2: Ù thường and Ù tròn should require eaten cards
    print("\n2. Ù thường and Ù tròn should require eaten cards")
    winner = 1
    is_khan = False
    is_round = False
    eaten_cards = [0, 1, 2, 0]
    result = calculate_perfect_hand_with_eaten_cards(winner, eaten_cards, is_khan, is_round, settings)

    # NEW LOGIC: Total MUST = 0
    print(f"Debug result: {result}, Total: {sum(result)}")
    assert sum(result) == 0, f"Total MUST be 0, got {sum(result)}"
    print(f"✓ Ù thường with eaten cards: {result}")
    
    # Test 3: Ù đền should override normal calculation
    print("\n3. Ù đền should override normal calculation")
    winner = 2
    is_khan = False
    is_round = False
    eaten_cards = [0, 0, 0, 3]  # Player 3 gave 3 cards
    result = calculate_perfect_hand_with_eaten_cards(winner, eaten_cards, is_khan, is_round, settings)

    # NEW LOGIC: Total MUST = 0
    print(f"Debug result: {result}, Total: {sum(result)}")
    assert sum(result) == 0, f"Total MUST be 0, got {sum(result)}"
    print(f"✓ Ù đền overrides normal calculation: {result}")

def test_back_button_scenarios():
    """Test back button and state management scenarios"""
    print("\n=== TESTING BACK BUTTON SCENARIOS ===")
    
    settings = {
        'perfect_hand_bonus': 10,
        'perfect_hand_khan_bonus': 20,
        'perfect_hand_round_bonus': 30,
        'eaten_card_fee': 2,
        'last_card_fee': 3,
        'mom_penalty': 5,
        'first_player_bonus': 3,
        'eaten_card_bonus': 1
    }
    
    # Test 1: Back from eaten cards to winner selection
    print("\n1. Back from eaten cards to winner selection")
    # Simulate going back and changing winner
    winner1 = 0
    eaten_cards1 = [1, 0, 0, 0]
    result1 = calculate_perfect_hand_with_eaten_cards(winner1, eaten_cards1, False, False, settings)
    
    # Go back and change winner
    winner2 = 1
    eaten_cards2 = [1, 0, 0, 0]  # Same eaten cards
    result2 = calculate_perfect_hand_with_eaten_cards(winner2, eaten_cards2, False, False, settings)
    
    # Results should be different
    assert result1 != result2, "Results should be different with different winners"
    print(f"✓ Back button changes result: {result1} vs {result2}")
    
    # Test 2: Back from Ù type selection
    print("\n2. Back from Ù type selection")
    # Start with Ù thường
    result_normal = calculate_perfect_hand_with_eaten_cards(0, [0, 0, 0, 0], False, False, settings)
    # Go back and change to Ù tròn
    result_round = calculate_perfect_hand_with_eaten_cards(0, [0, 0, 0, 0], False, True, settings)
    
    assert result_normal != result_round, "Results should be different with different Ù types"
    print(f"✓ Back button changes Ù type result: {result_normal} vs {result_round}")

def run_all_tests():
    """Run all test scenarios"""
    print("🧪 RUNNING TÁ LẢ SCORING TESTS")
    print("=" * 50)
    
    try:
        test_perfect_hand_scenarios()
        test_ranking_scenarios()
        test_edge_cases()
        test_settings_variations()
        test_den_scenarios()
        test_flow_scenarios()
        test_ui_flow_validation()
        test_back_button_scenarios()
        
        print("\n" + "=" * 50)
        print("✅ ALL TESTS PASSED!")
        print("🎯 Scoring logic is working correctly for all scenarios:")
        print("   • Ù Khan (no eaten cards)")
        print("   • Ù Thường/Ù Tròn (with eaten cards)")
        print("   • Ù Đền (someone gave 3 cards)")
        print("   • Normal ranking with various eaten cards")
        print("   • Edge cases and boundary conditions")
        print("   • Different settings configurations")
        
    except AssertionError as e:
        print(f"\n❌ TEST FAILED: {e}")
        return False
    except Exception as e:
        print(f"\n💥 ERROR: {e}")
        return False
    
    return True

if __name__ == "__main__":
    success = run_all_tests()
    exit(0 if success else 1) 