/*This file is part of NumGuesser.

        NumGuesser is free software: you can redistribute it and/or modify
        it under the terms of the GNU General Public License as published by
        the Free Software Foundation, either version 3 of the License, or
        (at your option) any later version.

        NumberGuesser is distributed in the hope that it will be useful,
        but WITHOUT ANY WARRANTY; without even the implied warranty of
        MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
        GNU General Public License for more details.

        You should have received a copy of the GNU General Public License
        along with NumGuesser.  If not, see <http://www.gnu.org/licenses/>.

*/

package com.numguesser.tonio_rpchp.numberguesser;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.os.Vibrator;
import android.os.VibrationEffect;
import android.os.Build;


import android.widget.Toast;
import java.util.Random;
import android.os.Handler;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

public class Guesser extends Activity {

    public int attempt;
    Random rand_num = new Random();
    int rnd;
    int clue_min = 0;
    int clue_max = 0;

    int MainCharacter;
    private FloatingActionMenu menu;
    private FloatingActionButton fab1;
    private FloatingActionButton fab2;
    //private FloatingActionButton fab3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guesser);

        start_game();

        final TextView tries_label = findViewById(R.id.tries_label);
        final TextView last_try_label = findViewById(R.id.last_try_label);
        final EditText number_txt = findViewById(R.id.number);
        final Button button = findViewById(R.id.guess_button);

        final android.widget.ImageView imageview = findViewById(R.id.imageView);

        fab1 =  this.findViewById(R.id.fab1);
        fab2 =  this.findViewById(R.id.fab2);
        //fab3 =  this.findViewById(R.id.fab3);

        fab1.setOnClickListener(clickListener);
        fab2.setOnClickListener(clickListener);
        //fab3.setOnClickListener(clickListener);


        menu = findViewById(R.id.fab_menu);
        menu.setClosedOnTouchOutside(true);

        SharedPreferences Preferences = getSharedPreferences("com.numguesser.com", Context.MODE_PRIVATE);
        MainCharacter = Preferences.getInt("avatar", R.drawable.android_genio);
        switch_image(MainCharacter, 0);

        imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                menu.close(true);
            }
        });

        button.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // Perform action on click

                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

                if (attempt != 0) {

                    try{
                        int num = Integer.parseInt(String.valueOf(number_txt.getText()));

                        if (num == 0 || num > 100){
                            Alert(getString(R.string.error), getString(R.string.in_range), 0);
                        }
                        else if (num == rnd) {
                            switch_image(R.drawable.android_genio_sad, 2);
                            Alert(getString(R.string.end_game), getString(R.string.win), 1);
                            last_try_label.setText(getString(R.string.win));
                            vibrate();
                        }
                        else{
                            attempt--;
                            tries_label.setText(String.format("%s %d ",getString(R.string.tries_text), attempt));

                            if (attempt > 0){
                                String result = check_rand(num, rnd);
                                last_try_label.setText(result);
                            }
                            else {
                                Alert(getString(R.string.end_game), getString(R.string.lose)+rnd, 1);
                                last_try_label.setText(String.format("%s %d ", getString(R.string.lose), rnd));
                            }
                        }
                    }
                    catch(Exception e){
                        Alert(getString(R.string.error), getString(R.string.in_range), 0);
                    }
                }

                number_txt.setText("");
            }
        });

    }

    public void Alert(String tittle, String message, Integer game_over){

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this );

        alertDialogBuilder.setTitle(tittle);
        alertDialogBuilder.setMessage(message);

        if (game_over == 1){
            alertDialogBuilder.setCancelable(false);
            // New Game
            alertDialogBuilder.setPositiveButton(getString(R.string.play),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            start_game();
                        }
                    });
            // Exit
            alertDialogBuilder.setNegativeButton(getString(R.string.exit),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        System.exit(1);
                    }
                });
        }
        else{
            alertDialogBuilder.setCancelable(false);
            alertDialogBuilder.setPositiveButton(getString(R.string.ok),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                        }
                    });
        }

        final AlertDialog alertDialog = alertDialogBuilder.create();

        new Handler().postDelayed(new
             Runnable() {
                @Override
                public void run() {
                    if (! alertDialog.isShowing()) {
                        alertDialog.show();
                    }
                }
            } , 700
        );

        if (game_over != 1){
            new Handler().postDelayed(new
              Runnable() {
                  @Override
                  public void run() {
                      if (alertDialog.isShowing()) {
                          alertDialog.dismiss();
                      }
                  }
              } , 2200
            );
        }

        //alertDialog.show();
    }

    public String check_rand(Integer input_number, Integer rnd){
        String Tittle = String.format("%s %d", getString(R.string.tries_text), attempt);
        String Text = getString(R.string.more);

        if (input_number > rnd){
            Text = getString(R.string.less);
            if (input_number < clue_max || clue_max == 0){
                clue_max = input_number;
            }
        }
        else{
            if (input_number > clue_min || clue_min == 0) {
                clue_min = input_number;
            }
        }

        if (input_number >= rnd - 3 && input_number <= rnd + 3) {
            Tittle = getString(R.string.on_fire);
            switch_image(R.drawable.android_genio_o, 1);
            vibrate();
        }
        else{
            switch_image(MainCharacter, 0);
        }

        Alert(Tittle, Text, 0);

        if (Tittle != ""){
            Text = String.format("%s, %s", Tittle, Text);
        }

        String msg = " X ";

        if (clue_min != 0){
            msg = String.format("%d < " + msg, clue_min);
        }
        if (clue_max != 0){
            msg = String.format(msg + " < %d", clue_max);
        }

        return msg;
    }

    public void vibrate(){
        Vibrator v = (Vibrator) getSystemService(this.VIBRATOR_SERVICE);
        v.vibrate(500);

        // Vibrate for 500 milliseconds
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            v.vibrate(VibrationEffect.createOneShot(500, VibrationEffect.DEFAULT_AMPLITUDE));
        } else {
            //deprecated in API 26
            v.vibrate(500);
        }
    }

    public void switch_image(Integer my_drawable_id, int mod){
        android.widget.ImageView back_img = findViewById(R.id.imageView);

        switch (MainCharacter){
            case R.drawable.android_genio:

                if (mod == 1){
                    my_drawable_id = R.drawable.android_genio_o;
                }
                if (mod == 2){
                    my_drawable_id = R.drawable.android_genio_sad;
                }

                break;
            case R.drawable.android_genio_old:

                if (mod == 1){
                    my_drawable_id = R.drawable.android_genio_old_o;
                }
                if (mod == 2){
                    my_drawable_id = R.drawable.android_genio_old_sad;
                }
                break;
        }

        back_img.setImageResource(my_drawable_id);
    }

    public void start_game(){
        switch_image(MainCharacter, 0);
        rnd = rand_num.nextInt(100) + 1;
        attempt = 5;
        clue_max = clue_min = 0;
        final TextView tries_label = findViewById(R.id.tries_label);
        final TextView last_try_label = findViewById(R.id.last_try_label);
        tries_label.setText(String.format("%s %d ",getString(R.string.tries_text), attempt));
        last_try_label.setText("");
    }

    private View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            SharedPreferences Preferences = getSharedPreferences("com.numguesser.com", Context.MODE_PRIVATE);
            SharedPreferences.Editor PreferencesEditor;
            PreferencesEditor = Preferences.edit();

            int img_resource = 0;

            switch (v.getId()) {
                case R.id.fab1:
                    img_resource = R.drawable.android_genio;
                    break;
                case R.id.fab2:
                    img_resource = R.drawable.android_genio_old;
                    break;
                /*
                case R.id.fab3:
                    fab1.setVisibility(View.GONE);
                    fab2.setVisibility(View.VISIBLE);
                    break;
                 */
            }

            Toast toast = Toast.makeText(getApplicationContext(), getString(R.string.change_msg), Toast.LENGTH_SHORT);
            toast.show();

            PreferencesEditor.putInt("avatar", img_resource);
            PreferencesEditor.commit();

            switch_image(img_resource, 0);
            MainCharacter = img_resource;

            menu.close(true);
        }
    };
}
