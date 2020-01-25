package org.github.otanikotani;

import static org.fusesource.jansi.Ansi.Color.BLACK;
import static org.fusesource.jansi.Ansi.Color.CYAN;
import static org.fusesource.jansi.Ansi.Color.GREEN;
import static org.fusesource.jansi.Ansi.Color.RED;
import static org.fusesource.jansi.Ansi.Color.WHITE;
import static org.fusesource.jansi.Ansi.Color.YELLOW;
import static org.fusesource.jansi.Ansi.ansi;

import org.fusesource.jansi.Ansi;

public class Clr {

  public static int colorlessLength(String str) {
    if (str == null || str.isBlank()) {
      return 0;
    }
    return str.replaceAll("\u001B\\[[\\d;]*[^\\d;]", "").length();
  }

  public static String colorlessTake(String str, int size) {
    int taken = 0;
    StringBuilder sb = new StringBuilder();
    boolean isVisibleSequence = true;
    boolean noMoreVisible = false;
    for (char c : str.toCharArray()) {
      if (c == 27) {
        isVisibleSequence = false;
      }
      if (!noMoreVisible || !isVisibleSequence) {
        sb.append(c);
      }
      if (isVisibleSequence) {
        taken++;
        if (taken >= size) {
          noMoreVisible = true;
        }
      }
      if (!isVisibleSequence && c == 'm') {
        isVisibleSequence = true;
      }
    }
    return sb.toString();
  }

  public static String colorlessRightPad(String str, int size) {
    int length = colorlessLength(str);
    int toAdd = size - length;
    if (toAdd <= 0) {
      return str;
    }

    return str + " ".repeat(toAdd);
  }

  public static String command(final Object obj) {
    return ansi().fg(WHITE).bold().a(obj).boldOff().reset().toString();
  }

  public static String error(final Object obj) {
    return colored(obj, RED);
  }

  public static String accent(final Object obj) {
    return colored(obj, YELLOW);
  }

  public static String comment(final Object obj) {
    return colored(obj, BLACK);
  }

  public static String link(Object obj) {
    return colored(obj, CYAN);
  }

  public static String title(Object obj) {
    return colored(obj, GREEN);
  }

  public static String success(Object obj) {
    return colored(obj, GREEN);
  }

  public static String bold(Object obj) {
    return ansi().bold().a(obj).reset().toString();
  }

  private static String colored(Object obj, Ansi.Color color) {
    return ansi().fg(color).a(obj).reset().toString();
  }
}
