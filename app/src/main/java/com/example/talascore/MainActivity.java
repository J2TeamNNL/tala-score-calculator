package com.example.talascore;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    // Constants for ViewFlipper children
    private static final int STEP_NAMES = 0;
    private static final int STEP_PERFECT_HAND = 1;
    private static final int STEP_FIRST_PLAYER = 2;
    private static final int STEP_RANKING = 3;
    private static final int STEP_RESULTS = 4;
    private static final int STEP_EATEN_CARDS = 5;

    // UI Components
    private ViewFlipper viewFlipper;
    private Toolbar toolbar;
    private ImageButton btnBack;
    private ImageButton btnHelp;
    
    // Step 1: Names
    private TextInputEditText[] etPlayerNames = new TextInputEditText[4];
    private Button btnConfirmNames;

    // Step 2: Perfect Hand
    private RadioGroup rgPerfectHand;
    private RadioGroup rgPerfectHandType;
    private Button btnNoPerfectHand;
    private Button btnConfirmPerfectHand;

    // Step 3: First Player Selection
    private RadioGroup rgFirstPlayer;
    private Button btnConfirmFirstPlayer;

    // Step 4: Ranking
    private TextView tvRankingTitle;
    private RadioGroup rgRankPlayer;
    private RadioGroup rgEatenCards;
    private Button btnConfirmRank;

    // Step 5: Results
    private TextView tvRoundResult, tvTotalScores;
    private Button btnNewRound, btnNewGameReset;
    
    // Step 5: Eaten Cards for Perfect Hand
    private RadioGroup[] rgPlayerEatenCards = new RadioGroup[4];
    private TextView[] tvPlayerEatenNames = new TextView[4];
    private Button btnConfirmEatenCards;
    
    // Game State
    private String[] playerNames = new String[4];
    private int[] totalScores = new int[4];
    private int[] roundScores = new int[4];
    private boolean[] wasMom = new boolean[4];
    private int[] cardsEaten = new int[4];
    private int[] lastCardsEaten = new int[4];
    private int perfectHandPlayer = -1;
    private boolean isPerfectHandKhan = false;
    private boolean isPerfectHandRound = false;
    private boolean isPerfectHand = false;
    private int currentRankingStep = 0;
    private int[] ranking = new int[4];
    private int firstPlayerIndex = -1;
    private boolean isPerfectHandDen = false; // Ù đền
    private int perfectHandDenPlayer = -1; // Ai đền
    
    // State for back navigation
    private int previousStep = -1;
    private int[] savedRanking = new int[4];
    private int savedCurrentRankingStep = 0;
    private int savedFirstPlayerIndex = -1;

    private SharedPreferences prefs;

    // Settings
    private int perfectHandBonus;
    private int perfectHandKhanBonus;
    private int perfectHandRoundBonus;
    private int eatenCardFee;
    private int lastCardFee;
    private int momPenalty;
    private int firstPlayerBonus;
    private int eatenCardBonus;
    private boolean autoSelectFirstPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        try {
            prefs = PreferenceManager.getDefaultSharedPreferences(this);
            
            initializeViews();
            setupToolbar();
            setupListeners();
            
            viewFlipper.setDisplayedChild(STEP_NAMES);
            loadSavedNames();
            loadSettings();
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi khởi tạo: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
    
    //region Initialization
    private void initializeViews() {
        try {
            viewFlipper = findViewById(R.id.view_flipper);
            toolbar = findViewById(R.id.toolbar);
            btnBack = findViewById(R.id.btn_back);
            btnHelp = findViewById(R.id.btn_help);

            // Names
            etPlayerNames[0] = findViewById(R.id.et_player1_name);
            etPlayerNames[1] = findViewById(R.id.et_player2_name);
            etPlayerNames[2] = findViewById(R.id.et_player3_name);
            etPlayerNames[3] = findViewById(R.id.et_player4_name);
            btnConfirmNames = findViewById(R.id.btn_confirm_names);

            // Perfect Hand
            rgPerfectHand = findViewById(R.id.rg_perfect_hand_winner);
            rgPerfectHandType = findViewById(R.id.rg_perfect_hand_type);
            btnNoPerfectHand = findViewById(R.id.btn_no_perfect_hand);
            btnConfirmPerfectHand = findViewById(R.id.btn_confirm_perfect_hand);

            // First Player Selection
            rgFirstPlayer = findViewById(R.id.rg_first_player);
            btnConfirmFirstPlayer = findViewById(R.id.btn_confirm_first_player);

            // Ranking
            tvRankingTitle = findViewById(R.id.tv_ranking_title);
            rgRankPlayer = findViewById(R.id.rg_rank_player);
            rgEatenCards = findViewById(R.id.rg_eaten_cards);
            btnConfirmRank = findViewById(R.id.btn_confirm_rank);

            // Results
            tvRoundResult = findViewById(R.id.tv_round_result);
            tvTotalScores = findViewById(R.id.tv_total_scores);
            btnNewRound = findViewById(R.id.btn_new_round);
            btnNewGameReset = findViewById(R.id.btn_new_game_reset);
            
            // Step 5: Eaten Cards for Perfect Hand
            rgPlayerEatenCards[0] = findViewById(R.id.rg_player1_eaten_cards);
            rgPlayerEatenCards[1] = findViewById(R.id.rg_player2_eaten_cards);
            rgPlayerEatenCards[2] = findViewById(R.id.rg_player3_eaten_cards);
            rgPlayerEatenCards[3] = findViewById(R.id.rg_player4_eaten_cards);
            tvPlayerEatenNames[0] = findViewById(R.id.tv_player1_eaten_name);
            tvPlayerEatenNames[1] = findViewById(R.id.tv_player2_eaten_name);
            tvPlayerEatenNames[2] = findViewById(R.id.tv_player3_eaten_name);
            tvPlayerEatenNames[3] = findViewById(R.id.tv_player4_eaten_name);
            btnConfirmEatenCards = findViewById(R.id.btn_confirm_eaten_cards);
            
            // Add input validation
            setupInputValidation();
        } catch (Exception e) {
            Toast.makeText(this, "Lỗi khởi tạo UI: " + e.getMessage(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
    
    private void setupToolbar() {
        setSupportActionBar(toolbar);
    }

    private void setupListeners() {
        btnConfirmNames.setOnClickListener(v -> onConfirmNames());
        btnNoPerfectHand.setOnClickListener(v -> onNoPerfectHand());
        btnConfirmPerfectHand.setOnClickListener(v -> onConfirmPerfectHand());
        btnConfirmFirstPlayer.setOnClickListener(v -> onConfirmFirstPlayer());
        btnConfirmRank.setOnClickListener(v -> onConfirmRank());
        btnNewRound.setOnClickListener(v -> startNewRound());
        btnNewGameReset.setOnClickListener(v -> startNewGame());
        btnConfirmEatenCards.setOnClickListener(v -> onConfirmEatenCards());
        btnBack.setOnClickListener(v -> goBack());
        btnHelp.setOnClickListener(v -> showHelpForCurrentStep());
        
        rgPerfectHand.setOnCheckedChangeListener((group, checkedId) -> {
            boolean hasWinner = checkedId != -1;
            rgPerfectHandType.setVisibility(hasWinner ? View.VISIBLE : View.GONE);
        });
    }
    //endregion

    //region Step Navigation
    private void onConfirmNames() {
        // Validate names
        for (int i = 0; i < 4; i++) {
            String name = etPlayerNames[i].getText().toString().trim();
            if (name.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập tên cho tất cả người chơi", Toast.LENGTH_SHORT).show();
                return;
            }
            playerNames[i] = name;
        }
        
        saveNames();
        setupPerfectHandStep();
        viewFlipper.setDisplayedChild(STEP_PERFECT_HAND);
        updateBackButton();
    }

    private void startNewRound() {
        // Reset round scores
        Arrays.fill(roundScores, 0);
        Arrays.fill(wasMom, false);
        Arrays.fill(cardsEaten, 0);
        Arrays.fill(lastCardsEaten, 0);
        perfectHandPlayer = -1;
        isPerfectHandKhan = false;
        isPerfectHandRound = false;
        isPerfectHand = false;
        isPerfectHandDen = false;
        perfectHandDenPlayer = -1;
        currentRankingStep = 0;
        Arrays.fill(ranking, -1);
        firstPlayerIndex = -1;
        
        setupPerfectHandStep();
        viewFlipper.setDisplayedChild(STEP_PERFECT_HAND);
        updateBackButton();
    }

    private void startNewGame() {
        // Reset everything
        Arrays.fill(totalScores, 0);
        startNewRound();
    }
    //endregion

    //region Perfect Hand Step
    private void setupPerfectHandStep() {
        rgPerfectHand.removeAllViews();
        
        // Add player options only (no "No perfect hand" in radio group)
        for (int i = 0; i < 4; i++) {
            RadioButton rb = new RadioButton(this);
            rb.setText(playerNames[i] + " Ù");
            rb.setTextColor(getResources().getColor(R.color.text_primary));
            rb.setId(i);
            rgPerfectHand.addView(rb);
        }
    }

    private void onNoPerfectHand() {
        isPerfectHand = false;
        perfectHandPlayer = -1;
        
        // Auto-select first player (first player in the list)
        firstPlayerIndex = 0;
        
        setupRankingStep();
        viewFlipper.setDisplayedChild(STEP_RANKING);
        updateBackButton();
    }

    private void onConfirmPerfectHand() {
        int checkedId = rgPerfectHand.getCheckedRadioButtonId();
        if (checkedId == -1) {
            Toast.makeText(this, "Vui lòng chọn người Ù hoặc chọn 'Không ai Ù'", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Perfect hand selected
        isPerfectHand = true;
        perfectHandPlayer = checkedId;
        
        // Check perfect hand type
        isPerfectHandKhan = false;
        isPerfectHandRound = false;

        if (rgPerfectHandType.getVisibility() == View.VISIBLE) {
            int typeId = rgPerfectHandType.getCheckedRadioButtonId();
            if (typeId == R.id.rb_perfect_hand_khan) {
                isPerfectHandKhan = true;
            } else if (typeId == R.id.rb_perfect_hand_round) {
                isPerfectHandRound = true;
            }
            // Default is normal perfect hand (rb_perfect_hand_normal)
        }
        
        // Only khan hand shows results immediately (no eaten cards)
        if (isPerfectHandKhan) {
            calculatePerfectHandScore(perfectHandPlayer, true);
            viewFlipper.setDisplayedChild(STEP_RESULTS);
        } else {
            // For normal and round perfect hands, ask for eaten cards for all players
            setupEatenCardsForPerfectHand();
            viewFlipper.setDisplayedChild(STEP_EATEN_CARDS);
        }
        updateBackButton();
    }
    //endregion

    //region First Player Selection
    private void setupFirstPlayerSelection() {
        rgFirstPlayer.removeAllViews();
        
        for (int i = 0; i < 4; i++) {
            RadioButton rb = new RadioButton(this);
            rb.setText(playerNames[i]);
            rb.setTextColor(getResources().getColor(R.color.text_primary));
            rb.setId(i);
            rgFirstPlayer.addView(rb);
        }
        
        // Auto-select first player (first in the list)
        rgFirstPlayer.check(0);
    }

    private void onConfirmFirstPlayer() {
        int checkedId = rgFirstPlayer.getCheckedRadioButtonId();
        if (checkedId == -1) {
            // If nothing selected, default to first player
            checkedId = 0;
        }
        
        firstPlayerIndex = checkedId;
        setupRankingStep();
        viewFlipper.setDisplayedChild(STEP_RANKING);
        updateBackButton();
    }
    //endregion

    //region Ranking Step
    private void setupRankingStep() {
        currentRankingStep = 0;
        Arrays.fill(ranking, -1);
        
        updateRankingTitle();
        setupRankingOptions();
    }

    private void updateRankingTitle() {
        String[] rankNames = {"Nhất", "Nhì", "Ba", "Tư"};
        tvRankingTitle.setText("Ai về " + rankNames[currentRankingStep] + "?");
    }

    private void setupRankingOptions() {
        rgRankPlayer.removeAllViews();
        
        List<Integer> availablePlayers = getAvailablePlayers();
        for (int playerIndex : availablePlayers) {
            RadioButton rb = new RadioButton(this);
            rb.setText(playerNames[playerIndex]);
            rb.setTextColor(getResources().getColor(R.color.text_primary));
            rb.setId(playerIndex);
            rgRankPlayer.addView(rb);
        }
        
        // Auto-select first available player
        if (availablePlayers.size() > 0) {
            rgRankPlayer.check(availablePlayers.get(0));
        }
    }

    private void onConfirmRank() {
        int checkedId = rgRankPlayer.getCheckedRadioButtonId();
        if (checkedId == -1) {
            // If nothing selected, select first available player
            List<Integer> availablePlayers = getAvailablePlayers();
            if (availablePlayers.size() > 0) {
                checkedId = availablePlayers.get(0);
            } else {
                Toast.makeText(this, "Vui lòng chọn người chơi", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        
        ranking[currentRankingStep] = checkedId;
        
        // Check eaten cards
        int eatenCardsId = rgEatenCards.getCheckedRadioButtonId();
        if (eatenCardsId == R.id.rb_mom) {
            wasMom[checkedId] = true;
            cardsEaten[checkedId] = 0;
            lastCardsEaten[checkedId] = 0;
        } else if (eatenCardsId == R.id.rb_eaten_1) {
            wasMom[checkedId] = false;
            cardsEaten[checkedId] = 1;
            lastCardsEaten[checkedId] = 0;
        } else if (eatenCardsId == R.id.rb_eaten_2) {
            wasMom[checkedId] = false;
            cardsEaten[checkedId] = 2;
            lastCardsEaten[checkedId] = 0;
        } else if (eatenCardsId == R.id.rb_eaten_1_last) {
            wasMom[checkedId] = false;
            cardsEaten[checkedId] = 1;
            lastCardsEaten[checkedId] = 1;
        } else if (eatenCardsId == R.id.rb_eaten_2_last) {
            wasMom[checkedId] = false;
            cardsEaten[checkedId] = 2;
            lastCardsEaten[checkedId] = 2;
        }
        
        currentRankingStep++;
        
        if (currentRankingStep < 4) {
            updateRankingTitle();
            setupRankingOptions();
        } else {
            // All players ranked, calculate scores
            calculateRankingScore();
            viewFlipper.setDisplayedChild(STEP_RESULTS);
            updateBackButton();
        }
    }
    //endregion

    //region Score Calculation
    private void calculatePerfectHandScore(int winnerIndex, boolean isKhan) {
        int[] roundPoints = new int[4];

        if (isKhan) {
            // Khan hand: each loser pays perfectHandKhanBonus points
            roundPoints[winnerIndex] = perfectHandKhanBonus * 3;
            for (int i = 0; i < 4; i++) {
                if (i != winnerIndex) {
                    roundPoints[i] = -perfectHandKhanBonus;
                }
            }
        } else {
            // Normal perfect hand: each loser pays perfectHandBonus points
            roundPoints[winnerIndex] = perfectHandBonus * 3;
            for (int i = 0; i < 4; i++) {
                if (i != winnerIndex) {
                    roundPoints[i] = -perfectHandBonus;
                }
            }
        }

        updateScores(roundPoints);
    }

    private void calculatePerfectHandScoreWithEatenCards() {
        int[] roundPoints = new int[4];
        
        // Calculate points based on perfect hand type
        int winnerPoints;
        int loserPenalty;

        if (isPerfectHandRound) {
            // Round perfect hand: each loser pays perfectHandRoundBonus points
            winnerPoints = perfectHandRoundBonus * 3;
            loserPenalty = -perfectHandRoundBonus;
        } else {
            // Normal perfect hand: each loser pays perfectHandBonus points
            winnerPoints = perfectHandBonus * 3;
            loserPenalty = -perfectHandBonus;
        }

        // Calculate points for each player
        for (int i = 0; i < 4; i++) {
            if (i == perfectHandPlayer) {
                // Winner gets bonus (no eaten card penalty for winner when Ù)
                roundPoints[i] = winnerPoints;
            } else {
                // Others get penalty and eaten card penalty
                roundPoints[i] = loserPenalty - (cardsEaten[i] * eatenCardFee);
            }
        }
        
        // Handle Ù đền (someone gave 3 cards to make someone else Ù)
        if (isPerfectHandDen) {
            // Calculate what the total penalty would be (without eaten card penalties)
            int basePenaltyPerPerson;
            if (isPerfectHandRound) {
                basePenaltyPerPerson = perfectHandRoundBonus; // Round Ù đền
            } else {
                basePenaltyPerPerson = perfectHandBonus; // Normal Ù đền
            }

            int totalBasePenalty = basePenaltyPerPerson * 3;

            // Reset all points
            Arrays.fill(roundPoints, 0);

            // Winner gets total penalty
            roundPoints[perfectHandPlayer] = totalBasePenalty;

            // Person who gave 3 cards pays total penalty
            roundPoints[perfectHandDenPlayer] = -totalBasePenalty;

            // Others get 0 points (already set by Arrays.fill)
        }
        
        updateScores(roundPoints);
    }

    private void calculateRankingScore() {
        int[] roundPoints = new int[4];
        
        // Calculate points based on ranking
        for (int i = 0; i < 4; i++) {
            int playerIndex = ranking[i];
            int rank = i + 1; // 1-based ranking
            
            // Base points for ranking
            int basePoints = 0;
            switch (rank) {
                case 1: basePoints = 6; break; // Nhất
                case 2: basePoints = -1; break; // Nhì
                case 3: basePoints = -2; break; // Ba
                case 4: basePoints = -3; break; // Tư
            }
            
            // Calculate eaten card points (progressive: 1st card = 1 point, 2nd card = 2 points)
            int eatenCardPoints = 0;
            for (int cardNum = 1; cardNum <= cardsEaten[playerIndex]; cardNum++) {
                eatenCardPoints += cardNum; // 1st card = 1, 2nd card = 2, etc.
            }

            // Calculate penalties for being eaten
            int eatenPenalty = cardsEaten[playerIndex] * eatenCardFee;
            int lastCardPenalty = lastCardsEaten[playerIndex] * lastCardFee;
            
            // Add mom penalty
            int momPenaltyPoints = wasMom[playerIndex] ? momPenalty : 0;
            
            // No first player bonus according to current rules
            int firstPlayerPoints = 0;
            
            roundPoints[playerIndex] = basePoints + eatenCardPoints - eatenPenalty - lastCardPenalty - momPenaltyPoints + firstPlayerPoints;
        }
        
        updateScores(roundPoints);
    }

    private void updateScores(int[] roundPoints) {
        // Update round scores
        System.arraycopy(roundPoints, 0, roundScores, 0, 4);
        
        // Update total scores
        for (int i = 0; i < 4; i++) {
            totalScores[i] += roundPoints[i];
        }
        
        // Display results
        displayResults();
    }

    private List<Integer> getAvailablePlayers() {
        List<Integer> available = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            boolean alreadyRanked = false;
            for (int j = 0; j < currentRankingStep; j++) {
                if (ranking[j] == i) {
                    alreadyRanked = true;
                    break;
                }
            }
            if (!alreadyRanked) {
                available.add(i);
            }
        }
        return available;
    }
    //endregion

    //region Data Persistence
    private void loadSavedNames() {
        for (int i = 0; i < 4; i++) {
            String savedName = prefs.getString("player_name_" + i, "");
            if (!savedName.isEmpty()) {
                etPlayerNames[i].setText(savedName);
                playerNames[i] = savedName;
            }
        }
    }

    private void saveNames() {
        SharedPreferences.Editor editor = prefs.edit();
        for (int i = 0; i < 4; i++) {
            editor.putString("player_name_" + i, playerNames[i]);
        }
        editor.apply();
    }

    private void setupInputValidation() {
        // Add text change listeners for name validation
        for (TextInputEditText et : etPlayerNames) {
            et.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {}
                @Override
                public void afterTextChanged(Editable s) {
                    // Enable/disable confirm button based on validation
                    boolean allNamesValid = true;
                    for (TextInputEditText nameEt : etPlayerNames) {
                        if (nameEt.getText().toString().trim().isEmpty()) {
                            allNamesValid = false;
                            break;
                        }
                    }
                    btnConfirmNames.setEnabled(allNamesValid);
                }
            });
        }
    }
    //endregion

    //region Menu and Help
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showHelpForCurrentStep() {
        int currentStep = viewFlipper.getDisplayedChild();
        String title = "";
        String message = "";
        
        switch (currentStep) {
            case STEP_NAMES:
                title = "Nhập tên người chơi";
                message = "Nhập tên cho 4 người chơi. Tên sẽ được lưu lại cho lần chơi sau.";
                break;
            case STEP_PERFECT_HAND:
                title = "Có ai Ù không?";
                message = "Chọn người Ù hoặc 'Không ai Ù' để tính điểm thường.\n\n" +
                         "• Ù thường: Có cạ, được cộng điểm theo cài đặt\n" +
                         "• Ù khan: Không có cạ, được cộng điểm cao hơn (không ăn cây)\n" +
                         "• Ù tròn: Ù với tất cả bài, được cộng điểm đặc biệt\n\n" +
                         "Nếu không ai Ù, app sẽ tự động chọn người đầu tiên và bắt đầu xếp hạng.";
                break;
            case STEP_FIRST_PLAYER:
                title = "Chọn người chơi đầu tiên";
                message = "Người chơi đầu tiên sẽ được cộng điểm nếu tất cả người chơi khác đều móm (không ăn cây nào).\n\n" +
                         "Người đầu tiên đã được chọn tự động, bạn có thể thay đổi nếu muốn.";
                break;
            case STEP_RANKING:
                title = "Xếp hạng và cây đã ăn";
                message = "Chọn người chơi về thứ tự và số cây họ đã ăn:\n\n" +
                         "• Móm: Không ăn cây nào, bị trừ điểm\n" +
                         "• Ăn cây: Bị trừ điểm theo số cây đã ăn\n" +
                         "• Chốt: Cây cuối cùng, bị trừ điểm nhiều hơn\n\n" +
                         "Người chơi đã được chọn tự động, bạn có thể thay đổi nếu muốn.";
                break;
            case STEP_EATEN_CARDS:
                title = "Cây đã ăn khi Ù";
                message = "Chọn số cây mỗi người đã ăn trong ván này:\n\n" +
                         "• Không ăn cây nào: 0 điểm phạt\n" +
                         "• Ăn 1 cây: Bị trừ điểm theo cài đặt\n" +
                         "• Ăn 2 cây: Bị trừ điểm gấp đôi\n" +
                         "• Ăn 3 cây (Ù đền): Người này phải đền thay cho cả làng\n\n" +
                         "Ù đền: Khi ai đó cho người khác ăn 3 cây làm họ Ù, người đó phải đền thay cho tất cả.";
                break;
            case STEP_RESULTS:
                title = "Kết quả";
                message = "Xem điểm ván này và tổng điểm. Có thể bắt đầu ván mới hoặc reset toàn bộ điểm.";
                break;
        }
        
        showHelpDialog(title, message);
    }

    private void showHelpDialog(String title, String message) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }
    //endregion

    //region Back Navigation
    private void goBack() {
        int currentStep = viewFlipper.getDisplayedChild();
        
        switch (currentStep) {
            case STEP_PERFECT_HAND:
                viewFlipper.setDisplayedChild(STEP_NAMES);
                break;
            case STEP_FIRST_PLAYER:
                viewFlipper.setDisplayedChild(STEP_PERFECT_HAND);
                break;
            case STEP_RANKING:
                if (currentRankingStep > 0) {
                    currentRankingStep--;
                    // Clear the ranking data for the step we're going back from
                    ranking[currentRankingStep] = -1;
                    updateRankingTitle();
                    setupRankingOptions();
                } else {
                    // If no perfect hand, go back to perfect hand step
                    // If perfect hand, go back to perfect hand step
                    viewFlipper.setDisplayedChild(STEP_PERFECT_HAND);
                }
                break;
            case STEP_RESULTS:
                if (isPerfectHand && !isPerfectHandKhan) {
                    viewFlipper.setDisplayedChild(STEP_EATEN_CARDS);
                } else if (isPerfectHand) {
                    viewFlipper.setDisplayedChild(STEP_PERFECT_HAND);
                } else {
                    viewFlipper.setDisplayedChild(STEP_PERFECT_HAND);
                }
                break;
            case STEP_EATEN_CARDS:
                viewFlipper.setDisplayedChild(STEP_PERFECT_HAND);
                break;
        }
        
        updateBackButton();
    }

    private void updateBackButton() {
        int currentStep = viewFlipper.getDisplayedChild();
        btnBack.setVisibility(currentStep == STEP_NAMES ? View.GONE : View.VISIBLE);
    }

    private void setupEatenCardsForPerfectHand() {
        // Set player names in the eaten cards step
        for (int i = 0; i < 4; i++) {
            tvPlayerEatenNames[i].setText(playerNames[i]);
        }

        // Reset all radio groups to default (0 eaten cards)
        int[] defaultIds = {
            R.id.rb_player1_eaten_0,
            R.id.rb_player2_eaten_0,
            R.id.rb_player3_eaten_0,
            R.id.rb_player4_eaten_0
        };

        for (int i = 0; i < 4; i++) {
            rgPlayerEatenCards[i].check(defaultIds[i]);
        }
    }

    private void onConfirmEatenCards() {
        // Reset Ù đền state
        isPerfectHandDen = false;
        perfectHandDenPlayer = -1;

        // Get eaten cards for all players
        int[][] eatenCardIds = {
            {R.id.rb_player1_eaten_0, R.id.rb_player1_eaten_1, R.id.rb_player1_eaten_2, R.id.rb_player1_eaten_3},
            {R.id.rb_player2_eaten_0, R.id.rb_player2_eaten_1, R.id.rb_player2_eaten_2, R.id.rb_player2_eaten_3},
            {R.id.rb_player3_eaten_0, R.id.rb_player3_eaten_1, R.id.rb_player3_eaten_2, R.id.rb_player3_eaten_3},
            {R.id.rb_player4_eaten_0, R.id.rb_player4_eaten_1, R.id.rb_player4_eaten_2, R.id.rb_player4_eaten_3}
        };

        for (int i = 0; i < 4; i++) {
            int eatenCardsId = rgPlayerEatenCards[i].getCheckedRadioButtonId();
            int eatenCards = 0;

            // Determine eaten cards based on radio button ID
            for (int j = 0; j < 4; j++) {
                if (eatenCardsId == eatenCardIds[i][j]) {
                    eatenCards = j;
                    break;
                }
            }

            // Check for Ù đền (3 cards eaten)
            if (eatenCards == 3) {
                // This is Ù đền - someone gave 3 cards to make someone else Ù
                // Only one person can be Ù đền per round
                if (!isPerfectHandDen) {
                    isPerfectHandDen = true;
                    perfectHandDenPlayer = i;
                }
            }

            cardsEaten[i] = eatenCards;
        }
        
        // Calculate perfect hand score with eaten cards penalty
        calculatePerfectHandScoreWithEatenCards();
        viewFlipper.setDisplayedChild(STEP_RESULTS);
        updateBackButton();
    }
    //endregion

    //region Settings
    private int getIntPreference(String key, int defaultValue) {
        try {
            String value = prefs.getString(key, String.valueOf(defaultValue));
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private void loadSettings() {
        // Load settings with correct keys from preferences.xml
        // These are penalty per person, not total
        perfectHandBonus = getIntPreference("perfect_hand_points", 5); // Default penalty per person for normal Ù
        perfectHandKhanBonus = getIntPreference("khan_hand_points", 7); // Default penalty per person for khan Ù
        perfectHandRoundBonus = getIntPreference("perfect_hand_round_bonus", 6); // Default penalty per person for round Ù
        eatenCardFee = getIntPreference("eaten_card_fee", 1);
        lastCardFee = getIntPreference("last_card_fee", 4);
        momPenalty = getIntPreference("mom_penalty_points", 4);
        firstPlayerBonus = getIntPreference("first_player_bonus", 0); // No first player bonus by default
        eatenCardBonus = getIntPreference("eaten_card_bonus", 1);
        autoSelectFirstPlayer = prefs.getBoolean("winner_is_first_player", false);
    }

    private void displayResults() {
        StringBuilder roundResult = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            String sign = roundScores[i] >= 0 ? "+" : "";
            roundResult.append(playerNames[i]).append(": ").append(sign).append(roundScores[i]).append("\n");
        }
        tvRoundResult.setText(roundResult.toString());
        
        StringBuilder totalResult = new StringBuilder();
        for (int i = 0; i < 4; i++) {
            totalResult.append(playerNames[i]).append(": ").append(totalScores[i]).append("\n");
        }
        tvTotalScores.setText(totalResult.toString());
    }
    //endregion
} 