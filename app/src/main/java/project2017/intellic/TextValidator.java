package project2017.intellic;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by aaronitzkovitz on 11/21/17.
 */

// use this class to check textfields AS the user is typing
public abstract class TextValidator implements TextWatcher {
    private final TextView textView;

    public TextValidator(EditText textbox){
        textView = textbox;
    }

    public abstract void validate(TextView textView, String text);

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        String text = textView.getText().toString();
        validate(textView, text);
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

}
