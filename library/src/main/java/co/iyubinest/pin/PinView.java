package co.iyubinest.pin;
interface PinView {

  PinField createField(int position);
  void add(PinField field);
  void focusOn(int position);
  void text(int position, String text);
  void ready(int position);
  void nonReady(int position);
  void hideKeyboard();
  interface NumberChangeListener {

    void onNumberChanged(String number);
  }

  interface NumberDeletedListener {

    void onNumberRemoved();
  }

  interface HideKeyboardListener {

    void hideKeyboardListener();
  }
}