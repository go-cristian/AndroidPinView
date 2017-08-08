package co.iyubinest.pin;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;

public final class PinEditView extends LinearLayout implements PinView {

  public static final int WIDTH = 35;
  private static final int SIZE = 6;
  private static final float TEXT_SIZE = 20;
  private PinFieldView[] fields;
  private Pin pin;
  private int initColor;
  private int focusColor;
  private int filledColor;
  private int textSize;
  private Drawable background;

  public PinEditView(Context context) {
    this(context, null);
  }

  public PinEditView(Context context, AttributeSet attrs) {
    super(context, attrs);
    readAttrs(attrs);
    init();
  }

  private void readAttrs(AttributeSet attributeSet) {
    TypedArray attrs = getContext().obtainStyledAttributes(attributeSet, R.styleable.PinEditView);
    try {
      int defaultTextSize =
          (int) (TEXT_SIZE * getContext().getResources().getDisplayMetrics().density);
      initColor = attrs.getColor(R.styleable.PinEditView_initColor, Color.BLACK);
      focusColor = attrs.getColor(R.styleable.PinEditView_focusColor, Color.WHITE);
      filledColor = attrs.getColor(R.styleable.PinEditView_filledColor, Color.GRAY);
      textSize = attrs.getDimensionPixelSize(R.styleable.PinEditView_textSize, defaultTextSize);
      background = attrs.getDrawable(R.styleable.PinEditView_inputbackground);
    } finally {
      attrs.recycle();
    }
  }

  private void init() {
    setOrientation(HORIZONTAL);
    MarginLayoutParams params = new MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
        ViewGroup.LayoutParams.WRAP_CONTENT);
    params.setMargins(0, 0, 0, 0);
    setLayoutParams(params);
    fields = new PinFieldView[SIZE];
    pin = new Pin(SIZE, this);
  }

  @Override public PinField createField(final int position) {
    PinFieldView field = fields[position] = new PinFieldView(getContext());
    field.setOnFocusChangeListener(new OnFocusChangeListener() {
      @Override public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus && pin != null) {
          pin.position(position);
        }
      }
    });
    field.setOnNumberChangeListener(new NumberChangeListener() {
      @Override public void onNumberChanged(String number) {
        pin.add(number);
      }
    });
    field.setOnNumberDeleteListener(new NumberDeletedListener() {
      @Override public void onNumberRemoved() {
        pin.remove();
      }
    });
    field.setHideKeyboardListener(new HideKeyboardListener() {
      @Override public void hideKeyboardListener() {
        hideKeyboard();
      }
    });
    float density = getContext().getResources().getDisplayMetrics().density;
    int paddingV = (int) (density * 3);
    int marginH = (int) (density * 2);
    LayoutParams params = new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
    params.weight = 1;
    params.setMargins(marginH, 0, marginH, 0);
    field.setLayoutParams(params);
    field.setMaxWidth((int) (WIDTH * density));
    field.setMinimumWidth((int) (WIDTH * density));
    field.setMovementMethod(null);
    field.setTextColor(initColor);
    field.setPadding(0, paddingV, 0, paddingV);
    field.setTextSize(textSize);
    if (background != null) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
        field.setBackground(background);
      } else {
        //noinspection deprecation
        field.setBackgroundDrawable(background);
      }
    }
    return field;
  }

  @Override public void add(PinField field) {
    addView((View) field);
  }

  @Override public void focusOn(int position) {
    fields[position].requestFocus();
    fields[position].setTextColor(focusColor);
  }

  public void showKeyboard() {
    InputMethodManager imm =
        (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
    imm.showSoftInput(fields[0], InputMethodManager.SHOW_IMPLICIT);
  }

  @Override public void text(int position, String text) {
    fields[position].setText(text);
  }

  @Override public void ready(int position) {
    fields[position].setTextColor(filledColor);
  }

  @Override public void nonReady(int position) {
    fields[position].setTextColor(initColor);
  }

  @Override public void hideKeyboard() {
    ((InputMethodManager) getContext().getSystemService(
        Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getWindowToken(), 0);
  }

  public String value() {
    return pin.value();
  }

  public boolean ready() {
    return pin.ready();
  }

  public void setReadyListener(ReadyListener readyListener) {
    pin.setReadyListener(readyListener);
  }

  public void clear() {
    pin.clear();
  }
}
