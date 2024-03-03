package error;
import java.awt.*;
import java.awt.event.*;

public class errorMessage extends Dialog {
  Panel panel1 = new Panel();
  BorderLayout borderLayout1 = new BorderLayout();

  public errorMessage(Frame frame, String title, boolean modal) {
    super(frame, title, modal);
    enableEvents(AWTEvent.WINDOW_EVENT_MASK);
    try  {
     add(panel1);
      panel1.add(new Label("Ошибка! Проверьте выражение ещё раз."));
      pack();
    }
    catch(Exception ex) {
      ex.printStackTrace();
    }
  }

  public errorMessage(Frame frame) {
    this(frame, "", false);
  }

  public errorMessage(Frame frame, boolean modal) {
    this(frame, "", modal);
  }

  public errorMessage(Frame frame, String title) {
    this(frame, title, false);
  }

  void jbInit() throws Exception {
    panel1.setLayout(borderLayout1);
  }

  protected void processWindowEvent(WindowEvent e) {
    if(e.getID() == WindowEvent.WINDOW_CLOSING) {
      cancel();
    }
    super.processWindowEvent(e);
  }

  void cancel() {
    dispose();
  }
}
