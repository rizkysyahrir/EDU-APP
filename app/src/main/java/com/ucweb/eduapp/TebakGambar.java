package com.ucweb.eduapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.speech.tts.UtteranceProgressListener;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TebakGambar extends Activity implements OnInitListener {

    //main layout
    LinearLayout main_layout, text_layout;
    //layouts with answer fields and letters buttons
    LinearLayout[] answer_line, letter_line;
    //guess and money text fields
    TextView guess_text, money_text;
    //guess and money counters
    int guess, money = 100;
    //constant=cost of one tip
    int tip_cost = 1;
    //imageview for pictures
    ImageView image;
    //tombol tip
    ImageButton tip_button;
    //tombol cek jawaban
    Button check_button;
    //constant=number of letter buttons
    static final int letters_count = 21;
    // max count of answer buttons in a row
    static final int answer_line_width = 9;
    //array nama gambar
    String[] images;
    //list for linking jawaban dan tombol huruf
    ArrayList<Point> link_list;
    //list helps to prevent changing buttons filled by tips
    ArrayList<Boolean> tip_list;
    //string for name of current image without spaces and extension
    String correct_answer = "";
    //contains number of letter in answer lines
    int letters_in_answer = 0;
    //current visible image number
    int cur_image = 0;
    //length of line with more letters
    int max_answer_line_length;
    //count of words in answer
    int words_in_answer;
    //klik untuk full screen gambar
    boolean isFullScreen = false;
    //save game
    SharedPreferences save;
    SharedPreferences.Editor editor;
    //Toasts for notifications
    Toast check_toast, tip_toast;
    //dekorasi tombol
    Point letter_button_size = new Point((int) Helper.getScreenSize().x / 7, (int) Helper.getScreenSize().x / 7);
    int letter_button_text_size = (int) Helper.getScreenSize().x / 20;
    int letter_button_text_color = Color.WHITE;
    Typeface letter_button_typeface = Typeface.DEFAULT_BOLD;
    int letter_button_background_id = R.drawable.letter_button_background;
    int answer_button_text_color = Color.BLACK;
    Typeface answer_button_typeface = Typeface.DEFAULT_BOLD;
    int answer_button_background_id = R.drawable.answer_button_background;
    Point tip_button_size = new Point((int) Helper.getScreenSize().x / 7, (int) Helper.getScreenSize().x / 7);
    int tip_button_background_id = R.drawable.tip_button_background;
    Point check_button_size = new Point((int) (Helper.getScreenSize().x / 2.5f), Helper.getScreenSize().x / 10);
    int check_button_text_size = Helper.getScreenSize().x / 50;
    int check_button_text_color = Color.BLACK;
    Typeface check_button_typeface = Typeface.DEFAULT_BOLD;
    int check_button_background_id = R.drawable.check_button;

    MediaPlayer mediaPlayer;
    private TextToSpeech repeatTTS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tebak_gambar);

        repeatTTS = new TextToSpeech(this, this);

        //init sound effects
        Helper.InitSounds(this, new String[]{"click", "correct", "wrong", "tip", "al_washi", "angkat_kaki" ,"babul_ilmi","baju_koko","bayar_zakat","beasiswa","bunga_sakura","empat_jam","hidup_senang"
                ,"ilmu_terapan","imbas_ketamakan","kemajuan_belajar","kepala_sekolah","lapangan_kerja","masa_reformasi","meja_sekolahan","menarik_kesimpulan","penghapal_quran","saksi_bisu","tuang_air","tip","ustad"});
        //initialization of all needed views
        initViews();
        //loading first image into image_view
        loadImage(images[cur_image]);
        //saving name of this image
        correct_answer = getUpperNameWithoutExtensionAndSpaces(images[cur_image]);
        //generating letter and answer buttons for current image name
        fillLetterButtons(correct_answer);
        generateAnswerButtons(getNameWithoutExtension(images[cur_image]));
    }

    void generateNewLevel() {
        initLinkingAndTipLists();
        cur_image++;
        loadImage(images[cur_image]);
        money += 2;
        money_text.setText( " " + money);
        guess_text.setText("Level" + " " + cur_image );
        letters_in_answer = 0;
        correct_answer = getUpperNameWithoutExtensionAndSpaces(images[cur_image]);
        fillLetterButtons(correct_answer);
        generateAnswerButtons(getNameWithoutExtension(images[cur_image]));
    }

    void initLinkingAndTipLists() {
        link_list = new ArrayList<Point>();
        tip_list = new ArrayList<Boolean>();
        for (int i = 0; i < 3 * letters_count; i++) {
            link_list.add(i, new Point(0, 0));
            tip_list.add(i, false);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    void initViews() {
        check_toast = new Toast(this);
        tip_toast = new Toast(this);
        tip_button = (ImageButton) findViewById(R.id.tip_button);
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) tip_button.getLayoutParams();
        lp.width = tip_button_size.x;
        lp.height = tip_button_size.y;
        tip_button.setLayoutParams(lp);
        tip_button.setBackgroundResource(tip_button_background_id);
        tip_button.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    getTip();
                    tip_button.setAlpha(128);
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    tip_button.setAlpha(255);
                }
                return false;
            }
        });
        main_layout = (LinearLayout) findViewById(R.id.main_layout);
        answer_line = new LinearLayout[3];
        answer_line[0] = (LinearLayout) findViewById(R.id.answer_line_1);
        answer_line[1] = (LinearLayout) findViewById(R.id.answer_line_2);
        answer_line[2] = (LinearLayout) findViewById(R.id.answer_line_3);
        letter_line = new LinearLayout[3];
        letter_line[0] = (LinearLayout) findViewById(R.id.letter_line_1);
        letter_line[1] = (LinearLayout) findViewById(R.id.letter_line_2);
        letter_line[2] = (LinearLayout) findViewById(R.id.letter_line_3);
        image = (ImageView) findViewById(R.id.question_image);
        setOnImageClickListener();
        check_button = (Button) findViewById(R.id.check_answer_button);
        check_button.setText("CEK");
        setOnCheckAnswerButtonClickListener();
        save = getSharedPreferences("SAVE_GAME", 0);
        editor = save.edit();
        if (save.contains("continue") && save.getBoolean("continue", false)) {
            images = save.getString("images", null).replaceAll("\'", "").split(",");
            cur_image = save.getInt("currentImage", 0);
            money = save.getInt("money", 0);
            for (int i = 0; i < images.length; i++) {
                System.out.println(images[i]);
            }
        } else {
            images = ShuffleImages(getImagesFromAssets());
            cur_image = 0;
            money = 0;
        }
        guess_text = (TextView) findViewById(R.id.guess_text);
        guess_text.setTypeface(Typeface.SERIF);
        guess_text.setTextSize(Helper.getScreenSize().x / 50);
        guess_text.setText("Level" + " " + cur_image );
        money_text = (TextView) findViewById(R.id.money_text);
        money_text.setTypeface(Typeface.SERIF);
        money_text.setTextSize(Helper.getScreenSize().x / 50);
        money_text.setText(" " + money);
        initLinkingAndTipLists();
        //load Admob Ads
    }

    String[] getImagesFromAssets() {
        String[] img_files = null;
        try {
            img_files = getAssets().list("pictures");
        } catch (IOException ex) {
            Logger.getLogger(GameActivity.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        return img_files;
    }

    void loadImage(String name) {
        try {
            InputStream ims = getAssets().open("pictures/" + name);
            Drawable d = Drawable.createFromStream(ims, null);
            image.setImageDrawable(d);
        } catch (IOException ex) {
            return;
        }
    }

    void setOnImageClickListener() {
        image.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    float[] imageMatrix = new float[9];
                    image.getImageMatrix().getValues(imageMatrix);
                    if (isImageTouched(event.getX(), event.getY(), imageMatrix) || isFullScreen) {
                        isFullScreen = !isFullScreen;
                        for (int i = 0; i < main_layout.getChildCount(); i++) {
                            if (main_layout.getChildAt(i).getId() != R.id.question_image) {
                                main_layout.getChildAt(i).setVisibility(isFullScreen ? View.GONE : View.VISIBLE);
                            } else {
                                RotateAnimation ra = new RotateAnimation(isFullScreen ? 0 : 90, isFullScreen ? 90 : 0,
                                        Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                                ra.setInterpolator(new LinearInterpolator());
                                ra.setDuration(1);
                                float sc = 1.6f;//getScreenSize().x / image.getHeight();
                                System.out.println("HEIGHT " + image.getHeight());
                                ScaleAnimation sa = new ScaleAnimation(isFullScreen ? 1 : sc, isFullScreen ? sc : 1,
                                        isFullScreen ? 1 : sc, isFullScreen ? sc : 1,
                                        Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                                sa.setDuration(1);
                                AnimationSet anim_set = new AnimationSet(true);
                                anim_set.setFillAfter(true);
                                anim_set.addAnimation(ra);
                                anim_set.addAnimation(sa);
                                image.startAnimation(anim_set);
                                //image.setScaleType(isFullScreen ? ScaleType.CENTER_INSIDE : ScaleType.FIT_CENTER);
                            }
                        }
                    }
                }
                return false;
            }
        });
    }

    void setOnCheckAnswerButtonClickListener() {
        check_button.setTypeface(check_button_typeface);
        check_button.setBackgroundResource(check_button_background_id);
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) check_button.getLayoutParams();
        lp.width = check_button_size.x;
        lp.height = check_button_size.y;
        check_button.setLayoutParams(lp);
        check_button.setTextColor(check_button_text_color);
        check_button.setTextSize(check_button_text_size);
        check_button.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    checkPlayersAnswer(letters_in_answer);
                    check_button.getBackground().setAlpha(128);
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    check_button.getBackground().setAlpha(255);
                }
                return false;
            }
        });
    }

    boolean isImageTouched(float x, float y, float[] im) {
        return (y <= image.getDrawable().getIntrinsicHeight() * im[4]) && (im[5] * im[4] <= y)
                && (x <= image.getDrawable().getIntrinsicWidth() * im[0]) && (im[2] * im[0] <= x);
    }

    void generateAnswerButtons(String answer) {
        //clear answer lines
        for (int i = 0; i < answer_line.length; i++) {
            answer_line[i].removeAllViews();
        }
        //number of words in answer
        int word_count = 1;
        //list of space positions in answer
        ArrayList<Integer> wp_list = new ArrayList<Integer>();
        //list of words lengths in answer
        ArrayList<Integer> wl_list = new ArrayList<Integer>();
        //calculating number of words and filling lists
        for (int i = 0; i < answer.length(); i++) {
            if (answer.charAt(i) == '_') {
                wp_list.add(i);
                if (word_count == 1) {
                    wl_list.add(i);
                } else {
                    wl_list.add(wp_list.get(word_count - 1) - wp_list.get(word_count - 2) - 1);
                }
                word_count++;
            }
        }
        wp_list.add(answer.length());
        if (word_count > 1) {
            wl_list.add(answer.length() - wp_list.get(word_count - 2) - 1);
        } else {
            wl_list.add(answer.length());
        }
        max_answer_line_length = getMaxAnswerLineLength(wl_list);
        words_in_answer = wl_list.size();
        int cur_word = 1;
        int k = 0;
        //addition buttons on answer lines depending on number and length of words
        while (cur_word <= word_count) {
            if (cur_word == 1) {
                for (int i = 0; i < wl_list.get(0); i++) {
                    addAnswerButton(answer_line[0], i, false);
                }
            } else {
                if (answer_line[k].getChildCount() + wl_list.get(cur_word - 1) <= answer_line_width) {
                    for (int i = 0; i < wl_list.get(cur_word - 1); i++) {
                        addAnswerButton(answer_line[k], i, true);
                    }
                } else {
                    if (k < 3) {
                        k++;
                    }
                    for (int i = 0; i < wl_list.get(cur_word - 1); i++) {
                        addAnswerButton(answer_line[k], i, false);
                    }
                }
            }
            cur_word++;
        }
        //assignment actions on answer button clicks
        setOnAnswerButtonsClickListeners();
    }

    int getMaxAnswerLineLength(ArrayList<Integer> wl_list) {
        int k = 0;
        ArrayList<Integer> buf = new ArrayList<Integer>();
        for (int i = 0; i < wl_list.size(); i++) {
            buf.add(i, 0);
        }
        int max = wl_list.get(0);
        for (int i = 1; i < wl_list.size(); i++) {
            if (max + wl_list.get(i) <= answer_line_width) {
                max += wl_list.get(i);
            } else {
                buf.add(k, max);
                max = wl_list.get(i);
                k++;
            }
        }
        for (int i = 0; i < buf.size(); i++) {
            if (buf.get(i) > max) {
                max = buf.get(i);
            }
        }
        return max;
    }

    void addAnswerButton(LinearLayout l, int index, boolean spaceNeeded) {
        Button b = new Button(this);
        b.setText("");
        int wh = (Helper.getScreenSize().x - 10 * words_in_answer) / (max_answer_line_length >= 8 ? max_answer_line_length : 8);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(wh, wh, 0);
        //lp.leftMargin = spaceNeeded?10:0;
        //lp.rightMargin = spaceNeeded?-10:0;
        lp.gravity = Gravity.CENTER_HORIZONTAL;
        lp.leftMargin = spaceNeeded && index == 0 ? 15 : 0;
        b.setTextColor(answer_button_text_color);
        b.setTypeface(answer_button_typeface);
        b.setLayoutParams(lp);
        b.setPadding(1, 1, 1, 1);
        b.setBackgroundResource(answer_button_background_id);
        b.setTextSize(TypedValue.COMPLEX_UNIT_PX, wh / 2f);
        l.addView(b);
    }

    void fillLetterButtons(String name) {
        //clear letter lines
        for (int i = 0; i < letter_line.length; i++) {
            letter_line[i].removeAllViews();
        }
        String nameWithoutSpaces = name.toUpperCase().replaceAll("_", "");
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        //generate shuffle string of letters from answer and some additional letters
        String letters = ShuffleLetters(nameWithoutSpaces
                + ShuffleLetters(alphabet).substring(0, letters_count - nameWithoutSpaces.length()));
        //addition buttons with letters from generated strings
        for (int i = 0; i < letters_count; i++) {
            Button b = new Button(this);
            b.setText(Character.toString(letters.charAt(i)));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(letter_button_size.x, letter_button_size.y, 0);
            b.setLayoutParams(lp);
            b.setPadding(1, 1, 1, 1);
            b.setTextColor(letter_button_text_color);
            b.setTypeface(letter_button_typeface);
            b.setBackgroundResource(letter_button_background_id);
            b.setTextSize(TypedValue.COMPLEX_UNIT_PX, letter_button_text_size);
            if (i < letters_count / 3) {
                letter_line[0].addView(b);
            } else if ((letters_count / 3 <= i) && (i < 2 * letters_count / 3)) {
                letter_line[1].addView(b);
            } else if ((2 * letters_count / 3 <= i) && (i < letters_count)) {
                letter_line[2].addView(b);
            }
        }
        //assignment actions on letters button clicks
        setOnLettersClickListeners();
    }

    void setOnLettersClickListeners() {
        for (int i = 0; i < letter_line.length; i++) {
            for (int j = 0; j < letter_line[i].getChildCount(); j++) {
                final Button b = (Button) letter_line[i].getChildAt(j);
                final Point letter_pos = new Point(i, j);
                b.setOnTouchListener(new View.OnTouchListener() {
                    public boolean onTouch(View v, MotionEvent event) {
                        if ((event.getAction() == MotionEvent.ACTION_MOVE) || (event.getAction() == MotionEvent.ACTION_DOWN)) {
                            if (!b.getText().toString().equals("") && letters_in_answer < correct_answer.length()) {
                                letters_in_answer++;
                                System.out.println("letters: " + letters_in_answer);
                                int free_pos = getFirstFreeAnswerButtonPosition();
                                Button a = findButtonByPos(answer_line, free_pos);
                                a.setText(b.getText());
                                hideButton(b);
                                link_list.set(free_pos, letter_pos);
                                soundclick();
                            }
                        }
                        return false;
                    }
                });
            }
        }
    }

    void checkPlayersAnswer(int current_pos) {
        if (current_pos == correct_answer.length()) {
            if (isCorrectAnswer()) {
                ShowToast(check_toast,  " " + getNameWithoutExtension(images[cur_image].replace("_", " ")).
                        toUpperCase() + "!", Gravity.CENTER, new Point(0, -50));
                //soundcorrect();
                //if not all images was shown we show next image
                if (cur_image < images.length - 1) {
                    generateNewLevel();
                    //else we show end-game menu
                } else {
                    Intent end_game = new Intent(TebakGambar.this, TebakSelesai.class);
                    startActivity(end_game);
                    finish();
                }
            }
            else {
                HideToast(check_toast, "Jawaban Anda Salah! Coba Lagi.", Gravity.CENTER, new Point(0, -50));
                soundwrong();
            }
        } else {
            HideToast(check_toast, "Jawaban Anda Salah! Coba Lagi.", Gravity.CENTER, new Point(0, -50));
            soundwrong();
        }
    }

    boolean isCorrectAnswer() {
        String player_answer = "";
        //concat all letters from answer buttons in one string
        for (int i = 0; i < answer_line.length; i++) {
            for (int j = 0; j < answer_line[i].getChildCount(); j++) {
                Button b = (Button) answer_line[i].getChildAt(j);
                player_answer += b.getText().toString();
            }
        }
        //compare player's answer with correct
        if (player_answer.equals(correct_answer)) {
            return true;
        } else {
            return false;
        }
    }

    int getFirstFreeAnswerButtonPosition() {
        int pos = 0;
        boolean isFound = false;
        for (int i = 0; i < answer_line.length; i++) {
            for (int j = 0; j < answer_line[i].getChildCount(); j++) {
                Button b = (Button) answer_line[i].getChildAt(j);
                if (b.getText().toString().equals("") && !isFound) {
                    pos = getAnswerButtonPos(i, j);
                    isFound = true;
                }
            }
        }
        return pos;
    }

    void setOnAnswerButtonsClickListeners() {
        for (int i = 0; i < answer_line.length; i++) {
            for (int j = 0; j < answer_line[i].getChildCount(); j++) {
                final Button b = (Button) answer_line[i].getChildAt(j);
                final int al_pos = getAnswerButtonPos(i, j);
                b.setOnTouchListener(new View.OnTouchListener() {
                    public boolean onTouch(View v, MotionEvent event) {
                        if ((event.getAction() == MotionEvent.ACTION_MOVE) || (event.getAction() == MotionEvent.ACTION_DOWN)) {
                            if (!b.getText().toString().equals("") && tip_list.get(al_pos).equals(false)) {
                                Button a = (Button) letter_line[link_list.get(al_pos).x].getChildAt(link_list.get(al_pos).y);
                                showButton(a, b.getText().toString());
                                Helper.playSound(getApplicationContext(), "click");
                                b.setText("");
                                letters_in_answer--;
                            }
                        }
                        return false;
                    }
                });
            }
        }
    }

    int getAnswerButtonPos(int row, int col) {
        int pos = 1;
        for (int i = 0; i <= row; i++) {
            if (i < row) {
                pos += answer_line[i].getChildCount();
            } else {
                pos += col;
            }
        }
        return pos;
    }

    Button findButtonByPos(LinearLayout[] line, int pos) {
        if (pos <= line[0].getChildCount()) {
            return (Button) line[0].getChildAt(pos - 1);
        } else if (pos <= line[0].getChildCount() + line[1].getChildCount()) {
            return (Button) line[1].getChildAt(pos - line[0].getChildCount() - 1);
        } else {
            return (Button) line[2].getChildAt(pos - line[0].getChildCount()
                    - line[1].getChildCount() - 1);
        }
    }

    String getNameWithoutExtension(String name) {
        return name.substring(0, name.indexOf('.'));
    }

    String getUpperNameWithoutExtensionAndSpaces(String name) {
        return getNameWithoutExtension(name.toUpperCase().replaceAll("_", ""));
    }

    String ShuffleLetters(String s) {
        ArrayList<Character> char_list = new ArrayList<Character>();
        for (int i = 0; i < s.length(); i++) {
            char_list.add(s.charAt(i));
        }
        Collections.shuffle(char_list);
        s = TextUtils.join("", char_list);
        return s;
    }

    String[] ShuffleImages(String[] imgs) {
        ArrayList<String> string_list = new ArrayList<String>();
        for (int i = 0; i < imgs.length; i++) {
            string_list.add(imgs[i]);
        }
        Collections.shuffle(string_list);
        return string_list.toArray(imgs);
    }

    void getTip() {
        if (money >= tip_cost) {
            //get position of button where we place correct letter
            int pos = getFirstIncorrectAnswerButtonPos();
            if (pos >= 0) {
                money -= tip_cost;
                money_text.setText(" " + money);
                //get button where we place correct letter
                Button button = findButtonByPos(answer_line, pos + 1);
                //remember that we can't more change this letter
                tip_list.add(pos + 1, true);
                if (button.getText().toString().equals("")) {
                    letters_in_answer++;
                }
                else {
                    //if button was busy with another letter then return this letter to its place
                    Button l_button = (Button) letter_line[link_list.get(pos + 1).x].getChildAt(link_list.get(pos + 1).y);
                    l_button.setText(button.getText());
                }
                int tip_letter_pos = findTipLetterButton(Character.toString(correct_answer.charAt(pos)));
                if (tip_letter_pos >= 0) {
                    Button tip_letter = findButtonByPos(letter_line, tip_letter_pos + 1);
                    tip_letter.setText("");
                } else {
                    letters_in_answer--;
                    findAndRemoveNeededAnswerLetter(Character.toString(correct_answer.charAt(pos)));
                }
                button.setText(Character.toString(correct_answer.charAt(pos)));
                button.setTextColor(Color.rgb(42, 0, 255));
                soundtip();
            }
        } else {
            BantuanToast(tip_toast, "Bantuan Anda Habis!", Gravity.TOP, new Point(0, 80));
            soundwrong();
        }
    }

    int getFirstIncorrectAnswerButtonPos() {
        int k = 0;
        boolean isFound = false;
        for (int i = 0; i < answer_line.length; i++) {
            for (int j = 0; j < answer_line[i].getChildCount(); j++) {
                Button b = (Button) answer_line[i].getChildAt(j);
                if (!b.getText().toString().equals(Character.toString(correct_answer.charAt(k))) && !isFound) {
                    isFound = true;
                    break;
                }
                k++;
            }
            if (isFound) {
                break;
            }
        }
        if (!isFound) {
            k = -1;
        }
        return k;
    }

    int findTipLetterButton(String letter) {
        int k = 0;
        boolean isFound = false;
        for (int i = 0; i < letter_line.length; i++) {
            for (int j = 0; j < letter_line[i].getChildCount(); j++) {
                Button b = (Button) letter_line[i].getChildAt(j);
                if (b.getText().toString().equals(letter) && !isFound) {
                    hideButton(b);
                    isFound = true;
                    break;
                }
                k++;
            }
            if (isFound) {
                break;
            }
        }
        if (!isFound) {
            k = -1;
        }
        return k;
    }

    void findAndRemoveNeededAnswerLetter(String needed_letter) {
        int k = 0;
        boolean isFound = false;
        for (int i = 0; i < answer_line.length; i++) {
            for (int j = 0; j < answer_line[i].getChildCount(); j++) {
                Button b = (Button) answer_line[i].getChildAt(j);
                if (!b.getText().toString().equals(Character.toString(correct_answer.charAt(k)))
                        && b.getText().toString().equals(needed_letter)) {
                    b.setText("");
                    isFound = true;
                    break;
                }
                k++;
            }
            if (isFound) {
                break;
            }
        }
    }

    void hideButton(Button b) {
        b.setText("");
        b.setVisibility(View.INVISIBLE);
    }

    void showButton(Button b, String text) {
        b.setVisibility(View.VISIBLE);
        b.setText(text);
    }

    @Override
    public void onBackPressed() {
        saveCurrentGame();
        Intent menu_intent = new Intent(TebakGambar.this, GameActivity.class);
        startActivity(menu_intent);
        finish();
    }

    @Override
    public void onPause() {
        saveCurrentGame();
        super.onPause();
    }

    void saveCurrentGame() {
        editor.putBoolean("gameSaved", true);
        editor.putString("images", arrayToString(images));
        editor.putInt("currentImage", cur_image);
        editor.putInt("money", money);
        editor.commit();
    }

    String arrayToString(String[] name) {
        StringBuilder sb = new StringBuilder();
        for (String s : name) {
            sb.append(s).append(',');
        }
        if (name.length != 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    void ShowToast(Toast toast, String message, int gravity, Point pos) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.customtoast,
                (ViewGroup) findViewById(R.id.toast_layout_root));
        // layout.setLayoutParams(lp);
        TextView text = (TextView) layout.findViewById(R.id.toast_text);
        text.setTextSize(Helper.getScreenSize().x / 30);
        text.setText(message);
        toast.setGravity(gravity, pos.x, pos.y);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        if (!toast.getView().isShown()) {
            toast.show();
            soundcorrect();
            repeatTTS.speak(message, TextToSpeech.QUEUE_ADD, null);
        }
    }



    void HideToast(Toast toast, String message, int gravity, Point pos) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.hidetoast,
                (ViewGroup) findViewById(R.id.toast_layout_root));
        // layout.setLayoutParams(lp);
        TextView text = (TextView) layout.findViewById(R.id.toast_text);
        text.setTextSize(Helper.getScreenSize().x / 30);
        text.setText(message);
        toast.setGravity(gravity, pos.x, pos.y);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        if (!toast.getView().isShown()) {
            toast.show();
        }
    }

    void BantuanToast(Toast toast, String message, int gravity, Point pos) {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.bantuan,
                (ViewGroup) findViewById(R.id.toast_layout_root));
        // layout.setLayoutParams(lp);
        TextView text = (TextView) layout.findViewById(R.id.toast_text);
        text.setTextSize(Helper.getScreenSize().x / 30);
        text.setText(message);
        toast.setGravity(gravity, pos.x, pos.y);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        if (!toast.getView().isShown()) {
            toast.show();
        }
    }

    @Override
    public void onInit(int i) {
        if (i == TextToSpeech.SUCCESS) {
            int result = repeatTTS.setLanguage(new Locale("id","ID"));
            if (result == TextToSpeech.LANG_MISSING_DATA ||
                    result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.e("TTS", "Lenguage not supported");
            }
        } else {
            Log.e("TTS", "Initialization failed");
        }
    }

    public void soundclick() {
        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.victory);
        if (mediaPlayer != null) {
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    mediaPlayer.reset();
                    mediaPlayer.release();
                }
            });

        }
    }
    //suara benar
    public void soundcorrect() {
        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.benarrr);
        if (mediaPlayer != null) {
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    mediaPlayer.reset();
                    mediaPlayer.release();
                }
            });

        }
    }
//suara salah
    public void soundwrong() {
        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.no);
        if (mediaPlayer != null) {
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    mediaPlayer.reset();
                    mediaPlayer.release();
                }
            });
        }
    }

    public void soundtip() {
        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.tip);
        if (mediaPlayer != null) {
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    mediaPlayer.reset();
                    mediaPlayer.release();
                }
            });
        }
    }
}