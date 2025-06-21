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
    """Test all perfect hand scenarios"""
    print("=== TESTING PERFECT HAND SCENARIOS ===")
    
    # Test settings
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
    
    # Test 1: Ù Khan (no eaten cards)
    print("\n1. Ù Khan - Player 0 Ù, no eaten cards")
    result = calculate_perfect_hand_khan(0, settings)
    expected = [20, -7, -7, -6]  # 20 for winner, -7 for others (20/3 rounded)
    assert result == expected, f"Expected {expected}, got {result}"
    print(f"✓ Ù Khan: {result}")
    
    # Test 2: Ù Thường - Player 1 Ù, all players ate some cards
    print("\n2. Ù Thường - Player 1 Ù, all players ate cards")
    eaten_cards = [1, 2, 0, 1]  # Player 0 ate 1, Player 1 ate 2, etc.
    result = calculate_perfect_hand_with_eaten_cards(1, eaten_cards, False, False, settings)
    expected = [-6, 6, -3, -5]  # 10 chia 3: -4, -3, -3, penalty dư phân bổ
    assert result == expected, f"Expected {expected}, got {result}"
    print(f"✓ Ù Thường with eaten cards: {result}")
    
    # Test 3: Ù Tròn - Player 2 Ù, all players ate cards
    print("\n3. Ù Tròn - Player 2 Ù, all players ate cards")
    eaten_cards = [0, 1, 1, 2]
    result = calculate_perfect_hand_with_eaten_cards(2, eaten_cards, False, True, settings)
    expected = [-10, -12, 28, -14]  # 30 chia 3: -10, -10, -10, penalty dư phân bổ
    assert result == expected, f"Expected {expected}, got {result}"
    print(f"✓ Ù Tròn with eaten cards: {result}")
    
    # Test 4: Ù Đền - Player 3 Ù, Player 0 gave 3 cards
    print("\n4. Ù Đền - Player 3 Ù, Player 0 gave 3 cards")
    eaten_cards = [3, 0, 0, 0]  # Player 0 gave 3 cards (Ù đền)
    result = calculate_perfect_hand_with_eaten_cards(3, eaten_cards, False, False, settings)
    # Player 0 pays for everyone, Player 3 gets everything
    expected = [-18, 0, 0, 18]  # Tổng penalty thực tế
    assert result == expected, f"Expected {expected}, got {result}"
    print(f"✓ Ù Đền: {result}")
    
    # Test 5: Ù Đền with Ù Tròn - Player 1 Ù tròn, Player 2 gave 3 cards
    print("\n5. Ù Đền with Ù Tròn - Player 1 Ù tròn, Player 2 gave 3 cards")
    eaten_cards = [0, 0, 3, 0]  # Player 2 gave 3 cards
    result = calculate_perfect_hand_with_eaten_cards(1, eaten_cards, False, True, settings)
    # Player 2 pays for everyone, Player 1 gets everything
    expected = [0, 36, -36, 0]  # Tổng penalty thực tế
    assert result == expected, f"Expected {expected}, got {result}"
    print(f"✓ Ù Đền with Ù Tròn: {result}")

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
    expected = [1, -6, -6, -5]  # Cập nhật theo logic penalty mới
    assert result == expected, f"Expected {expected}, got {result}"
    print(f"✓ Normal ranking: {result}")
    
    # Test 2: All players mom - first player gets bonus
    print("\n2. All players mom - first player gets bonus")
    ranking = [1, 0, 2, 3]  # Player 1 is 1st, Player 0 is 2nd, etc.
    eaten_cards = [0, 0, 0, 0]
    last_cards = [0, 0, 0, 0]
    was_mom = [True, True, True, True]
    first_player = 1
    result = calculate_ranking_score(ranking, eaten_cards, last_cards, was_mom, first_player, settings)
    expected = [-1, 4, -3, -3]  # Cập nhật theo logic penalty mới
    assert result == expected, f"Expected {expected}, got {result}"
    print(f"✓ All mom with first player bonus: {result}")
    
    # Test 3: Mixed scenario with last cards
    print("\n3. Mixed scenario with last cards")
    ranking = [2, 3, 0, 1]  # Player 2 is 1st, Player 3 is 2nd, etc.
    eaten_cards = [1, 2, 0, 1]
    last_cards = [0, 1, 0, 1]  # Player 1 and 3 ate last cards
    was_mom = [False, False, True, False]
    first_player = 2
    result = calculate_ranking_score(ranking, eaten_cards, last_cards, was_mom, first_player, settings)
    expected = [-5, -13, 1, -1]  # Cập nhật theo logic penalty mới
    assert result == expected, f"Expected {expected}, got {result}"
    print(f"✓ Mixed scenario with last cards: {result}")

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
    eaten_cards = [3, 0, 0, 0]  # Player 0 gave 3 cards
    result = calculate_perfect_hand_with_eaten_cards(1, eaten_cards, False, False, settings)
    expected = [-18, 18, 0, 0]  # Tổng penalty thực tế
    assert result == expected, f"Expected {expected}, got {result}"
    print(f"✓ Maximum eaten cards (Ù đền): {result}")
    
    # Test 2: Zero eaten cards for all players
    print("\n2. Zero eaten cards for all players")
    eaten_cards = [0, 0, 0, 0]
    result = calculate_perfect_hand_with_eaten_cards(2, eaten_cards, False, False, settings)
    expected = [-4, -3, 10, -3]  # Cập nhật theo logic penalty mới
    assert result == expected, f"Expected {expected}, got {result}"
    print(f"✓ Zero eaten cards: {result}")
    
    # Test 3: All players ate maximum cards (edge case)
    print("\n3. All players ate maximum cards (edge case)")
    eaten_cards = [2, 2, 2, 2]  # All ate 2 cards (max before Ù đền)
    result = calculate_perfect_hand_with_eaten_cards(0, eaten_cards, False, False, settings)
    expected = [4, -7, -7, -6]  # Cập nhật theo logic penalty mới
    assert result == expected, f"Expected {expected}, got {result}"
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
    expected = [-14, 40, -13, -13]  # Cập nhật theo logic penalty mới
    assert result == expected, f"Expected {expected}, got {result}"
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
    expected = [-9, 0, 9, 0]  # Tổng penalty thực tế
    assert result == expected, f"Expected {expected}, got {result}"
    print(f"✓ Low penalty Ù đền: {result}")

def calculate_perfect_hand_khan(winner_index, settings):
    """Calculate perfect hand khan score"""
    bonus = settings['perfect_hand_khan_bonus']
    
    # Calculate penalty for each loser
    # Total penalty = bonus, divided among 3 losers
    total_penalty = bonus
    base_penalty = total_penalty // 3
    remainder = total_penalty % 3
    
    scores = [0] * 4
    scores[winner_index] = bonus
    
    # Distribute penalty among losers
    penalty_count = 0
    for i in range(4):
        if i != winner_index:
            if penalty_count < remainder:
                scores[i] = -(base_penalty + 1)
            else:
                scores[i] = -base_penalty
            penalty_count += 1
    
    return scores

def calculate_perfect_hand_with_eaten_cards(winner_index, eaten_cards, is_khan, is_round, settings):
    """Calculate perfect hand score with eaten cards consideration"""
    if is_khan:
        return calculate_perfect_hand_khan(winner_index, settings)
    
    # Determine base bonus
    if is_round:
        base_bonus = settings['perfect_hand_round_bonus']
    else:
        base_bonus = settings['perfect_hand_bonus']
    
    # Check for Ù đền (someone ate 3 cards)
    den_players = [i for i, cards in enumerate(eaten_cards) if cards == 3]
    if den_players:
        # Ù đền scenario: all den_players share the penalty, winner gets all
        n = 4
        scores = [0] * n
        # Calculate what each player would normally get
        normal_scores = []
        for i in range(n):
            if i == winner_index:
                score = base_bonus - (eaten_cards[i] * settings['eaten_card_fee'])
            else:
                normal_scores.append(-base_bonus // 3 - (eaten_cards[i] * settings['eaten_card_fee']))
        # Total penalty to pay
        total_penalty = sum(abs(s) for s in normal_scores)
        # Split penalty among all den_players
        base_penalty = total_penalty // len(den_players)
        remainder = total_penalty % len(den_players)
        for idx, i in enumerate(den_players):
            if idx < remainder:
                scores[i] = -(base_penalty + 1)
            else:
                scores[i] = -base_penalty
        scores[winner_index] = total_penalty
        return scores
    else:
        # Normal perfect hand with eaten cards
        n = 4
        scores = [0] * n
        # Calculate penalty for losers
        total_penalty = base_bonus
        base_penalty = total_penalty // 3
        remainder = total_penalty % 3
        penalty_count = 0
        for i in range(n):
            if i == winner_index:
                scores[i] = base_bonus - (eaten_cards[i] * settings['eaten_card_fee'])
            else:
                penalty = base_penalty
                if penalty_count < remainder:
                    penalty += 1
                scores[i] = -penalty - (eaten_cards[i] * settings['eaten_card_fee'])
                penalty_count += 1
        return scores

def calculate_ranking_score(ranking, eaten_cards, last_cards, was_mom, first_player, settings):
    """Calculate ranking score"""
    scores = [0] * 4
    
    # Base ranking points
    ranking_points = [3, 1, -1, -3]  # 1st, 2nd, 3rd, 4th
    
    for i, player_index in enumerate(ranking):
        rank = i  # 0-based ranking
        base_points = ranking_points[rank]
        
        # Add eaten card penalties
        eaten_penalty = eaten_cards[player_index] * settings['eaten_card_fee']
        last_card_penalty = last_cards[player_index] * settings['last_card_fee']
        
        # Add mom penalty
        mom_penalty_points = settings['mom_penalty'] if was_mom[player_index] else 0
        
        # Add first player bonus if applicable
        first_player_points = 0
        if player_index == first_player:
            # Check if all others are mom
            all_others_mom = all(was_mom[j] for j in range(4) if j != player_index)
            if all_others_mom:
                first_player_points = settings['first_player_bonus']
        
        scores[player_index] = base_points - eaten_penalty - last_card_penalty - mom_penalty_points + first_player_points
    
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
    
    # Test 1: Ù đền thường - Player 0 gave 3 cards, Player 1 Ù
    print("\n1. Ù đền thường - Player 0 gave 3 cards, Player 1 Ù")
    eaten_cards = [3, 0, 0, 0]  # Player 0 gave 3 cards
    result = calculate_perfect_hand_with_eaten_cards(1, eaten_cards, False, False, settings)
    # Player 0 pays for everyone, Player 1 gets everything
    expected = [-10, 10, 0, 0]  # Player 0 pays total penalty, Player 1 gets it
    assert result == expected, f"Expected {expected}, got {result}"
    print(f"✓ Ù đền thường: {result}")
    
    # Test 2: Ù đền khan - Player 2 gave 3 cards, Player 0 Ù khan
    print("\n2. Ù đền khan - Player 2 gave 3 cards, Player 0 Ù khan")
    eaten_cards = [0, 0, 3, 0]  # Player 2 gave 3 cards
    result = calculate_perfect_hand_with_eaten_cards(0, eaten_cards, True, False, settings)
    # Player 2 pays for everyone, Player 0 gets everything
    expected = [20, 0, -20, 0]  # Player 2 pays total penalty, Player 0 gets it
    assert result == expected, f"Expected {expected}, got {result}"
    print(f"✓ Ù đền khan: {result}")
    
    # Test 3: Ù đền tròn - Player 3 gave 3 cards, Player 1 Ù tròn
    print("\n3. Ù đền tròn - Player 3 gave 3 cards, Player 1 Ù tròn")
    eaten_cards = [0, 0, 0, 3]  # Player 3 gave 3 cards
    result = calculate_perfect_hand_with_eaten_cards(1, eaten_cards, False, True, settings)
    # Player 3 pays for everyone, Player 1 gets everything
    expected = [0, 30, 0, -30]  # Player 3 pays total penalty, Player 1 gets it
    assert result == expected, f"Expected {expected}, got {result}"
    print(f"✓ Ù đền tròn: {result}")
    
    # Test 4: Multiple players gave 3 cards (edge case)
    print("\n4. Multiple players gave 3 cards (edge case)")
    eaten_cards = [3, 0, 3, 0]  # Player 0 and 2 both gave 3 cards
    result = calculate_perfect_hand_with_eaten_cards(1, eaten_cards, False, False, settings)
    # Both players pay, Player 1 gets everything
    expected = [-10, 20, -10, 0]  # Both pay total penalty, Player 1 gets it
    assert result == expected, f"Expected {expected}, got {result}"
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
    expected = [-10, 10, 0, 0]
    assert result == expected, f"Expected {expected}, got {result}"
    print(f"✓ Complete Ù đền flow: {result}")
    
    # Test 2: Complete Ù khan flow (immediate result)
    print("\n2. Complete Ù khan flow (immediate result)")
    winner = 2
    is_khan = True
    is_round = False
    # Ù khan doesn't need eaten cards input
    result = calculate_perfect_hand_khan(winner, settings)
    expected = [-7, -7, 20, -6]
    assert result == expected, f"Expected {expected}, got {result}"
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
    expected = [5, -3, -6, -6]
    assert result == expected, f"Expected {expected}, got {result}"
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
    expected = [20, -7, -7, -6]  # Same as Ù khan without eaten cards
    assert result == expected, f"Expected {expected}, got {result}"
    print(f"✓ Ù khan ignores eaten cards: {result}")
    
    # Test 2: Ù thường and Ù tròn should require eaten cards
    print("\n2. Ù thường and Ù tròn should require eaten cards")
    winner = 1
    is_khan = False
    is_round = False
    eaten_cards = [0, 1, 2, 0]
    result = calculate_perfect_hand_with_eaten_cards(winner, eaten_cards, is_khan, is_round, settings)
    expected = [-3, 8, -7, -3]  # Winner gets 10-2=8, others get -3-eaten penalty
    assert result == expected, f"Expected {expected}, got {result}"
    print(f"✓ Ù thường with eaten cards: {result}")
    
    # Test 3: Ù đền should override normal calculation
    print("\n3. Ù đền should override normal calculation")
    winner = 2
    is_khan = False
    is_round = False
    eaten_cards = [0, 0, 0, 3]  # Player 3 gave 3 cards
    result = calculate_perfect_hand_with_eaten_cards(winner, eaten_cards, is_khan, is_round, settings)
    expected = [0, 0, 10, -10]  # Player 3 pays, Player 2 gets
    assert result == expected, f"Expected {expected}, got {result}"
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