package co.iyubinest.pin;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Selection;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.util.AttributeSet;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.BaseInputConnection;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.EditText;
import android.widget.TextView;

final class PinFieldView extends EditText implements PinField {

  //no implementation to disable the copy/paste functionality
  private final ActionMode.Callback customSelectionActionModeCallback = new ActionMode.Callback() {
    @Override public boolean onCreateActionMode(ActionMode mode, Menu menu) {
      return false;
    }

    @Override public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
      return false;
    }

    @Override public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
      return false;
    }

    @Override public void onDestroyActionMode(ActionMode mode) {
    }
  };
  private PinView.NumberChangeListener numberChangeListener;
  private PinView.NumberDeletedListener numberDeletedListener;
  private final OnKeyListener keyListener = new OnKeyListener() {
    @Override public boolean onKey(View v, int keyCode, KeyEvent event) {
      if (event.getAction() != KeyEvent.ACTION_DOWN) {
        return false;
      }
      if (event.getUnicodeChar()
          == (int) EditableAccomodatingLatinIMETypeNullIssues.ONE_UNPROCESSED_CHARACTER.charAt(0)) {
        return true;
      }
      if (keyCode == KeyEvent.KEYCODE_DEL && numberDeletedListener != null) {
        numberDeletedListener.onNumberRemoved();
      } else if (numberChangeListener != null) {
        if (!Character.isDigit(event.getDisplayLabel())) {
          return false;
        }
        numberChangeListener.onNumberChanged(String.valueOf(event.getDisplayLabel()));
      }
      return true;
    }
  };
  private PinView.HideKeyboardListener hideKeyboardListener;
  private final EditText.OnEditorActionListener editorActionListener =
      new EditText.OnEditorActionListener() {
        @Override public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
          if (event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
            hideKeyboardListener.hideKeyboardListener();
            return true;
          }
          return false;
        }
      };
  private InputConnection baseInputConnection;

  public PinFieldView(Context context) {
    this(context, null);
  }

  public PinFieldView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init();
  }

  private void init() {
    baseInputConnection = new InputConnectionAccomodatingLatinIMETypeNullIssues(this, false);
    String defaultValue = "0";
    setText(defaultValue);
    setCursorVisible(false);
    if (getBackground() != null) {
      getBackground().mutate().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
    }
    setOnKeyListener(keyListener);
    setRawInputType(InputType.TYPE_CLASS_NUMBER);
    setOnEditorActionListener(editorActionListener);
    setFilters(disableSpecialCharacters());
    setCustomSelectionActionModeCallback(customSelectionActionModeCallback);
    setGravity(Gravity.CENTER_HORIZONTAL);
  }

  public void setOnNumberChangeListener(PinView.NumberChangeListener numberChangeListener) {
    this.numberChangeListener = numberChangeListener;
  }

  public void setOnNumberDeleteListener(PinView.NumberDeletedListener numberDeletedListener) {
    this.numberDeletedListener = numberDeletedListener;
  }

  public void setHideKeyboardListener(PinView.HideKeyboardListener hideKeyboardListener) {
    this.hideKeyboardListener = hideKeyboardListener;
  }

  @Override public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
    outAttrs.actionLabel = null;
    outAttrs.inputType = InputType.TYPE_CLASS_NUMBER;
    outAttrs.imeOptions = EditorInfo.IME_ACTION_DONE;
    return baseInputConnection;
  }

  public InputFilter[] disableSpecialCharacters() {
    InputFilter filter = new InputFilter() {
      @Override
      public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart,
          int dend) {
        for (int i = start; i < end; i++) {
          if (!Character.isDigit(source.charAt(i))) {
            return "";
          }
        }
        return null;
      }
    };
    InputFilter[] FilterArraym = new InputFilter[2];
    FilterArraym[0] = filter;
    FilterArraym[1] = new InputFilter.LengthFilter(1);
    return FilterArraym;
  }

  private static class EditableAccomodatingLatinIMETypeNullIssues extends SpannableStringBuilder {

    //This character must be ignored by your onKey() code.
    public static CharSequence ONE_UNPROCESSED_CHARACTER = "/";

    EditableAccomodatingLatinIMETypeNullIssues(CharSequence source) {
      super(source);
    }

    @Override public SpannableStringBuilder replace(final int spannableStringStart,
        final int spannableStringEnd, CharSequence replacementSequence, int replacementStart,
        int replacementEnd) {
      if (replacementEnd > replacementStart) {
        super.replace(0, length(), "", 0, 0);
        return super.replace(0, 0, replacementSequence, replacementStart, replacementEnd);
      } else if (spannableStringEnd > spannableStringStart) {
        super.replace(0, length(), "", 0, 0);
        return super.replace(0, 0, ONE_UNPROCESSED_CHARACTER, 0, 1);
      }
      return super.replace(spannableStringStart, spannableStringEnd, replacementSequence,
          replacementStart, replacementEnd);
    }
  }

  private class InputConnectionAccomodatingLatinIMETypeNullIssues extends BaseInputConnection {

    private Editable editable;

    InputConnectionAccomodatingLatinIMETypeNullIssues(View targetView, boolean mutable) {
      super(targetView, mutable);
    }

    @Override public Editable getEditable() {
      if (editable == null) {
        editable = new EditableAccomodatingLatinIMETypeNullIssues(
            EditableAccomodatingLatinIMETypeNullIssues.ONE_UNPROCESSED_CHARACTER);
        Selection.setSelection(editable, 1);
      } else {
        int length = editable.length();
        if (length == 0) {
          editable.append(EditableAccomodatingLatinIMETypeNullIssues.ONE_UNPROCESSED_CHARACTER);
          Selection.setSelection(editable, 1);
        }
      }
      return editable;
    }

    @Override public boolean deleteSurroundingText(int beforeLength, int afterLength) {
      if (beforeLength == 1 && afterLength == 0) {
        return super.sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL)) && super
            .sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL));
      } else {
        return super.deleteSurroundingText(beforeLength, afterLength);
      }
    }
  }
}
