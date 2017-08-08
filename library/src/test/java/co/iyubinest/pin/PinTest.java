package co.iyubinest.pin;
import co.iyubinest.pin.Pin;
import co.iyubinest.pin.PinField;
import co.iyubinest.pin.PinView;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class PinTest {

  private final int SIZE = 6;
  @Mock private PinView view;
  private Pin pin;

  @Before public void before() throws Exception {
    MockitoAnnotations.initMocks(this);
    pin = new Pin(SIZE, view);
  }

  @Test public void createFields() throws Exception {
    verify(view, times(SIZE)).createField(anyInt());
    verify(view, times(SIZE)).add(any(PinField.class));
  }

  @Test public void setsFirstPositionWithZeroAndReturns_100000() {
    pin.position(0);
    pin.add("1");
    verify(view, times(1)).focusOn(eq(1));
    verify(view, times(1)).text(eq(0), eq("1"));
    assertEquals("100000", pin.value());
    assertFalse(pin.ready());
  }

  @Test public void setsSecondPositionWithZeroAndReturns_020000() {
    pin.position(1);
    pin.add("2");
    verify(view, times(1)).focusOn(eq(2));
    verify(view, times(1)).text(eq(1), eq("2"));
    assertEquals("020000", pin.value());
    assertFalse(pin.ready());
  }

  @Test public void setsLastPositionWithZeroAndReturns_000006() {
    pin.position(5);
    pin.add("6");
    verify(view, times(1)).focusOn(eq(0));
    verify(view, times(1)).text(eq(5), eq("6"));
    assertEquals("000006", pin.value());
    assertFalse(pin.ready());
  }

  @Test public void setsEntireNumberReturns_123456() {
    pin.position(0);
    pin.add("1");
    pin.add("2");
    pin.add("3");
    pin.add("4");
    pin.add("5");
    pin.add("6");
    assertEquals("123456", pin.value());
    assertTrue(pin.ready());
  }

  @Test public void setsEntireNumberStartingFromSecondReturns_123456() {
    pin.position(1);
    pin.add("2");
    pin.add("3");
    pin.add("4");
    pin.add("5");
    pin.add("6");
    pin.position(0);
    pin.add("1");
    assertEquals("123456", pin.value());
    assertTrue(pin.ready());
  }

  @Test public void deleteSecondPosition() {
    pin.add("3");
    pin.add("2");
    pin.remove();
    verify(view, times(1)).focusOn(eq(0));
    verify(view, times(2)).focusOn(eq(1));
    verify(view, times(1)).focusOn(eq(2));
    assertEquals("320000", pin.value());
  }

  @Test public void deleteExactPosition() {
    pin.add("1");
    pin.add("2");
    pin.add("3");
    pin.add("4");
    pin.add("5");
    pin.add("6");
    pin.position(2);
    pin.remove();
    verify(view, times(1)).text(eq(2), eq("0"));
    verify(view, times(2)).focusOn(eq(2));
    assertEquals("120456", pin.value());
  }

  @Test public void clear() {
    pin.add("1");
    pin.add("2");
    pin.add("3");
    pin.add("4");
    pin.add("5");
    pin.add("6");
    pin.clear();
    assertEquals("000000", pin.value());
    assertFalse(pin.ready());
  }

  @Test public void deleteFirstPosition() {
    pin.remove();
    verify(view, times(1)).focusOn(eq(0));
    assertEquals("000000", pin.value());
  }
}
