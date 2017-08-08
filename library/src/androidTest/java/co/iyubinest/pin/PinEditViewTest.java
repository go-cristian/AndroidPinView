package co.iyubinest.pin;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

@RunWith(AndroidJUnit4.class) public class PinEditViewTest {

  @Rule public ActivityTestRule<PinEditViewTestActivity> mActivityRule =
      new ActivityTestRule<>(PinEditViewTestActivity.class);

  public static Matcher<View> nthChildOf(final Matcher<View> parentMatcher,
      final int childPosition) {
    return new TypeSafeMatcher<View>() {
      @Override public void describeTo(Description description) {
        description.appendText("with " + childPosition + " child view of type parentMatcher");
      }

      @Override public boolean matchesSafely(View view) {
        if (!(view.getParent() instanceof ViewGroup)) {
          return parentMatcher.matches(view.getParent());
        }
        ViewGroup group = (ViewGroup) view.getParent();
        return parentMatcher.matches(view.getParent()) && group.getChildAt(childPosition)
            .equals(view);
      }
    };
  }

  @Test public void sizeIsDefaultSix() throws Throwable {
    final PinEditViewTestActivity activity = mActivityRule.getActivity();
    final PinEditView pinView = new PinEditView(activity);
    mActivityRule.runOnUiThread(new Runnable() {
      @Override public void run() {
        activity.setContentView(pinView);
      }
    });
    InstrumentationRegistry.getInstrumentation().waitForIdleSync();
    assertNotNull(pinView);
    assertThat(pinView.getChildCount(), is(6));
  }

  @Test public void focusAndDefaultValue() throws Throwable {
    final PinEditViewTestActivity activity = mActivityRule.getActivity();
    final PinEditView pinView = new PinEditView(activity);
    mActivityRule.runOnUiThread(new Runnable() {
      @Override public void run() {
        activity.setContentView(pinView);
      }
    });
    InstrumentationRegistry.getInstrumentation().waitForIdleSync();
    checkPinValue(pinView, 0, "0");
    checkPinFocus(pinView, 0, true);
    checkPinValue(pinView, 1, "0");
    checkPinFocus(pinView, 1, false);
    checkPinValue(pinView, 2, "0");
    checkPinFocus(pinView, 2, false);
    checkPinValue(pinView, 3, "0");
    checkPinFocus(pinView, 3, false);
    checkPinValue(pinView, 4, "0");
    checkPinFocus(pinView, 4, false);
    checkPinValue(pinView, 5, "0");
    checkPinFocus(pinView, 5, false);
  }

  @Test public void writesAndGainFocusWithFirstPosition() throws Throwable {
    final PinEditViewTestActivity activity = mActivityRule.getActivity();
    final PinEditView pinView = new PinEditView(activity);
    mActivityRule.runOnUiThread(new Runnable() {
      @Override public void run() {
        activity.setContentView(pinView);
      }
    });
    InstrumentationRegistry.getInstrumentation().waitForIdleSync();
    writeValue(0, "1");
    checkPinValue(pinView, 0, "1");
    checkPinFocus(pinView, 1, true);
  }

  @Test public void writesAndGainFocusWithSecondPosition() throws Throwable {
    final PinEditViewTestActivity activity = mActivityRule.getActivity();
    final PinEditView pinView = new PinEditView(activity);
    mActivityRule.runOnUiThread(new Runnable() {
      @Override public void run() {
        activity.setContentView(pinView);
      }
    });
    InstrumentationRegistry.getInstrumentation().waitForIdleSync();
    writeValue(1, "2");
    checkPinValue(pinView, 1, "2");
    checkPinFocus(pinView, 2, true);
  }

  @Test public void writesAndGainFocusWithThirdPosition() throws Throwable {
    final PinEditViewTestActivity activity = mActivityRule.getActivity();
    final PinEditView pinView = new PinEditView(activity);
    mActivityRule.runOnUiThread(new Runnable() {
      @Override public void run() {
        activity.setContentView(pinView);
      }
    });
    InstrumentationRegistry.getInstrumentation().waitForIdleSync();
    writeValue(2, "3");
    checkPinValue(pinView, 2, "3");
    checkPinFocus(pinView, 3, true);
  }

  @Test public void isComplete() throws Throwable {
    final PinEditViewTestActivity activity = mActivityRule.getActivity();
    final PinEditView pinView = new PinEditView(activity);
    mActivityRule.runOnUiThread(new Runnable() {
      @Override public void run() {
        activity.setContentView(pinView);
      }
    });
    InstrumentationRegistry.getInstrumentation().waitForIdleSync();
    assertThat("checking value on field", pinView.ready(), is(false));
    writeValue(0, "1");
    writeValue(1, "2");
    writeValue(2, "3");
    writeValue(3, "4");
    writeValue(4, "5");
    writeValue(5, "6");
    Thread.sleep(200);
    assertThat("checking value on field", pinView.value(), is("123456"));
    assertThat("checking value on field", pinView.ready(), is(true));
    checkPinFocus(pinView, 5, true);
  }

  @Test public void clearView() throws Throwable {
    final PinEditViewTestActivity activity = mActivityRule.getActivity();
    final PinEditView pinView = new PinEditView(activity);
    mActivityRule.runOnUiThread(new Runnable() {
      @Override public void run() {
        activity.setContentView(pinView);
      }
    });
    InstrumentationRegistry.getInstrumentation().waitForIdleSync();
    writeValue(0, "1");
    writeValue(1, "2");
    writeValue(2, "3");
    writeValue(3, "4");
    writeValue(4, "5");
    writeValue(5, "6");
    mActivityRule.runOnUiThread(new Runnable() {
      @Override public void run() {
        pinView.clear();
      }
    });
    Thread.sleep(200);
    assertThat("checking value on field", pinView.value(), is("000000"));
  }

  private void writeValue(int position, String value) {
    onView(nthChildOf(withClassName(equalTo(PinEditView.class.getName())), position)).perform(
        typeText(value));
  }

  private void checkPinFocus(PinEditView pinView, int position, boolean focus) {
    EditText child = (EditText) pinView.getChildAt(position);
    assertThat(String.format("checking value on %s", position), child.hasFocus(), is(focus));
  }

  private void checkPinValue(PinEditView pinView, int position, String value) {
    EditText child = (EditText) pinView.getChildAt(position);
    assertThat(String.format("checking value on %s", position), child.getText().toString(),
        is(value));
  }
}