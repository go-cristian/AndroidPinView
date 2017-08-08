package co.iyubinest.pin;
import java.util.regex.Pattern;

final class Pin {

  private final int size;
  private final PinView view;
  private int position;
  private boolean[] ready;
  private StringBuilder value = new StringBuilder();
  private ReadyListener readyListener;
  private Pattern pattern;

  Pin(int size, PinView view) {
    this.size = size;
    this.view = view;
    ready = new boolean[size];
    for (int i = 0; i < size; i++) {
      value.insert(i, "0");
      view.add(view.createField(i));
    }
    position(0);
    pattern = Pattern.compile("[0-9]");
  }

  void add(String number) {
    if (validate(number)) {
      ready[position] = true;
      value.replace(position, position + 1, number);
      view.text(position, number);
      throwReady();
      if (position == size - 1) view.hideKeyboard();
      if (position + 1 < size) position(position + 1);
    } else {
      ready[position] = false;
      view.text(position, "0");
      value.replace(position, position + 1, "0");
    }
  }

  void remove() {
    final char charToRemove = value.charAt(position);
    ready[position] = false;
    view.text(position, "0");
    throwReady();
    value.replace(position, position + 1, "0");
    if (charToRemove == '0' && position - 1 >= 0) position(position - 1);
  }

  String value() {
    return value.toString();
  }

  boolean ready() {
    for (boolean state : ready) {
      if (!state) return false;
    }
    return true;
  }

  void position(int position) {
    positionReady(this.position);
    this.position = position;
    view.focusOn(position);
  }

  private void positionReady(int position) {
    if (ready[position]) {
      view.ready(position);
    } else {
      view.nonReady(position);
    }
  }

  void setReadyListener(ReadyListener readyListener) {
    this.readyListener = readyListener;
  }

  private void throwReady() {
    if (readyListener != null) {
      boolean ready = ready();
      readyListener.onReady(ready);
    }
  }

  private boolean validate(String value) {
    return pattern.matcher(value).matches();
  }

  void clear() {
    for (int i = 0; i < size; i++) {
      value.replace(i, i + 1, "0");
      view.text(i, "0");
      ready[i] = false;
    }
    throwReady();
    position(0);
  }
}
