import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import java.util.Vector;
import error.*;

public class LRAnalysis extends Applet implements ActionListener {
	LeftPanel lp;
  RightPanel rp;
	TextField src;
	String szFontName = "TimesRoman";
  Button lb, rb;
  String bc;
//  Image background;//
//  String path;//

	public String getAppletInfo() {
		return "Name: Leftside and Rightside Analysir";
	}
	public void init() {
    bc = getParameter("gnd");
    if (bc == null) bc = "10740218";
//    path = getParameter("path");
//    if (path == null)
//      path = "Art/bg024.jpg";
//    URL base = getDocumentBase();
//    background = getImage(base, path);
		int sd = 200, ad = 50;
    setLayout(new BorderLayout());
    Panel p = new Panel();
    p.setLayout(new GridLayout(0, 2, 10, 10));
//    p.setBackground(new Color(163, 225, 250));
    p.setBackground(new Color(Integer.parseInt(bc)));
		lp = new LeftPanel(sd, ad, "(a+b)*c+a+(a+b)", this);
    p.add(lp);
    rp = new RightPanel(sd, ad, "(a+b)*c+a+(a+b)", this);
    p.add(rp);
    add("Center", p);
    Panel control = new Panel();
    add("North", control);
		lb = new Button("Левосторонний вывод");
		lb.addActionListener(this);
    control.add("South", lb);
		control.add(src = new TextField("(a+b)*c+a+(a+b)", 21));
    rb = new Button("Правосторонний вывод");
		rb.addActionListener(this);
    control.add("South", rb);
//    control.setBackground(new Color(163, 225, 250));
    control.setBackground(new Color(Integer.parseInt(bc)));
	}
	public void destroy() {
		remove(lp);
	}
	public static void main(String args[]) {
		Frame f = new Frame("LeftSide and RightSide Analysis");
		LRAnalysis lra = new LRAnalysis();
    lra.init();
		lra.start();
    f.add("Center", lra);
		f.setSize(600, 400);
		f.show();
	}
	public void processEvent(AWTEvent e) {
        	if (e.getID() == Event.WINDOW_DESTROY) {
            		System.exit(0);
	        }
	}
  public void actionPerformed (ActionEvent ev) {
		String label = ev.getActionCommand();
    if (label.equals("Левосторонний вывод")) {
    	lp.expression = src.getText();
      lb.setEnabled(false);
      lp.startAnalysis();
    }
    else {
      rp.expression = src.getText();
      rb.setEnabled(false);
      rp.startAnalysis();
    }
  }
}
///
class LeftPanel extends Panel implements Runnable, AdjustmentListener {
  errorMessage err;
  protected Thread analyser = null;
	protected String[] parArray = new String[2];
	protected String syntesis = "S";
  protected String expression = "";
	protected Vector syntes = new Vector();
  public boolean m_tut = false, m_flash = false;
  protected String m_str;
  protected LRAnalysis parent = null;
  protected int sd, nd, ad, m_code, m_gram, m_curstr = 0, m_cntFlash = 6,
  m_currow = 0, m_cntMove = 10, offset = 0, l = 0;
  protected double m_X, m_Y;
  protected String[][] grammatic = { {"S->", "S+T"}, {"S->", "S-T"}, {"S->", "T"},
            {"T->", "T*F"}, {"T->", "T/F"}, {"T->", "F"}, {"F->", "(S)"},
            {"F->", "a"}, {"F->", "b"}, {"F->", "c"}};

  Scrollbar sb;
	public LeftPanel(int sd, int ad, String expr, LRAnalysis parent) {
		this.sd = sd;
    this.ad = ad;
    this.parent = parent;
    this.expression = expr;
    setLayout(new BorderLayout());
    sb = new Scrollbar(Scrollbar.VERTICAL);
    sb.addAdjustmentListener(this);;
    sb.setEnabled(true);
    add("East", sb);
    calcAnalysis();
	}

  Image offImage = null;
  Dimension offscreensize;
  Graphics offgraphics;

	public void paint(Graphics g) {
    update(g);
	}
  public synchronized void update(Graphics g) {
    Dimension d = getSize();
    Font fnt1 = new Font("Helvetica", Font.ITALIC, 12);
    Font fnt2 = new Font("Courier", Font.PLAIN, 13);
    Font fnt3 = new Font("Courier", Font.BOLD, 14);
    if ((offImage == null) || (d.width != offscreensize.width) || (d.height != offscreensize.height)) {
      offImage = createImage(d.width, d.height);
      offscreensize = d;
      offgraphics = offImage.getGraphics();
    }
//    offgraphics.setColor(new Color(163, 225, 250));
    offgraphics.setColor(new Color(Integer.parseInt(parent.bc)));
    offgraphics.fillRect(0, 0, d.width, d.height);
    offgraphics.setColor(Color.black);
    offgraphics.setFont(fnt1);
//    for (int i = 0; i < d.width / parent.background.getWidth(this) + 1; i++) {//
//      for (int j = 0; j < d.height / parent.background.getHeight(this) + 1; j++) {//
//        offgraphics.drawImage(parent.background, i * parent.background.getWidth(this), j * parent.background.getHeight(this), this);//
//      }//
//    }//
    offgraphics.drawString("Процес вывода выражения", 10, 15 - offset);
    offgraphics.drawString("Грамматика", 2 * d.width / 3, 15 - offset);
    offgraphics.setFont(fnt2);
    offgraphics.drawRect(0, 0, d.width  - 1, d.height - 1);
    for (int i = 0; i < grammatic.length; i++) {
      if (m_tut && m_code == 2 && m_gram == i) {}
      else
        offgraphics.drawString(grammatic[i][0] + grammatic[i][1], 2 * d.width / 3, 30 + i * 14 - offset);
    }
    if (m_tut == false) {
      for (int i = 0; i < syntes.size(); i++)
        offgraphics.drawString((String)syntes.elementAt(i), 20, 30 + 14 * i - offset);
    }
    else { // Tutorial
      for (int i = 0; i < m_curstr; i++) {
        offgraphics.drawString((String)syntes.elementAt(i), 20, 30 + 14 * i - offset);
      }
      if (m_currow > 0 && (m_code == 4 || m_code ==5)) {
        offgraphics.drawString(expression.substring(0, m_currow), 20, 30 + 14 * m_curstr - offset);
      }
      switch (m_code) {
        case 1: {
          if (m_flash) {
            offgraphics.setFont(fnt3);
            offgraphics.setColor(Color.red);
          }
          else {
            offgraphics.setFont(fnt2);
          }
          offgraphics.drawString(m_str, 20 + 8 * m_currow, 30 + 14 * (m_curstr - 1) - offset);
          break;
        }
        case 2: {
          offgraphics.drawString(grammatic[m_gram][0], 2 * d.width / 3, 30 + 14 * m_gram - offset);
          if (m_flash) {
            offgraphics.setFont(fnt3);
            offgraphics.setColor(Color.red);
          }
          else {
            offgraphics.setFont(fnt2);
          }
            offgraphics.drawString(grammatic[m_gram][1], 2 * d.width / 3 + grammatic[m_gram][0].length() * 8, 30 + 14 * m_gram - offset);
          break;
        }
        case 3: {
          offgraphics.setColor(Color.green);
          offgraphics.drawString(expression.substring(0, m_currow), 20, (int)m_Y - offset);
          break;
        }
        case 4: {
          offgraphics.setColor(Color.blue);
          offgraphics.drawString(grammatic[m_gram][1], (int)m_X, (int)m_Y - offset);
          break;
        }
        case 5: {
          offgraphics.drawString(grammatic[m_gram][1], 20 + m_currow * 8, 30 + m_curstr * 14 - offset);
          offgraphics.setColor(Color.green);
          String s = syntes.elementAt(m_curstr - 1).toString();
            offgraphics.drawString(s.substring(m_currow + 1), (int)m_X, (int)m_Y - offset);
          break;
        }
      }
    }
    g.drawImage(offImage, 0, 0, null);
  }
	public void calcAnalysis() {
    l = 0;
		if ((expression.length() + 1) % 2 == 0 && syntax()) {
			if (syntes.isEmpty() == false) {
				syntes.removeAllElements();
      }
      sb.setMaximum(0);
      m_curstr = 0;
      m_currow = 0;
      m_code = 0;
			parArray = analysis(expression, syntesis);
      syntes.addElement(parArray[1]);
      repaint();
      m_curstr++;
      if (m_tut) {
        repaint();
        try {
          analyser.sleep(sd);
        }
        catch (InterruptedException e) {
        }
      }
		}
    else {
      errorMessage err = new errorMessage(new Frame("Error!"), "Error.", true);
      err.resize(300, 100);
      err.show();
    }
	}
  public void start() {
    if (analyser != null)
      analyser.resume();
  }
  public void stop() {
    if (analyser != null)
      analyser.suspend();
  }
	public String[] analysis(String expression, String syntesis) {
		int skob = 0;
		char sign = 0;
		boolean p = true;
		String right = "",left = expression;
    String retArray[] = new String [2];

    syntes.addElement(syntesis);
    m_curstr++;
    if (m_curstr*14 > 300) {
      sb.setMaximum(sb.getMaximum() + 14);
    }
    if (m_tut) {
        m_code = 1; m_str = "S";
        for (int  j = 0; j < m_cntFlash; j++) {
        if ((j % 2) == 0)
          m_flash = true;
        else
          m_flash = false;
        repaint();
        try {
          analyser.sleep(sd);
        }
        catch (InterruptedException e) {
        }
      }
    }
    m_code = 0;
		for (int i = expression.length()-1; i >= 0; i--) {
			sign = expression.charAt(i);
			if (sign == '(') skob++;
			else
				if (sign == ')') skob--;
				else
					if (skob == 0)
						if (((sign == '+') || (sign == '-')) && i>0 && i<expression.length() - 1) {
              if (m_tut) {
                m_code = 2;
                for (int j = 0; j < m_cntFlash; j++) {
                  if (sign == '+')
                    m_gram = 0;
                  else
                    m_gram = 1;
                  if ((j % 2) == 0)
                    m_flash = true;
                  else
                    m_flash = false;
                  repaint();
                  try {
                    analyser.sleep(sd);
                  }
                  catch (InterruptedException e) {
                  }
                }
                if (m_currow > 0) {
                  m_code = 3;
                  for (int j = 0; j < 14; j++) {
                    m_Y = (m_curstr - 1) * 14 + 30 + j;
                    repaint();
                    try {
                      analyser.sleep(ad);
                    }
                    catch (InterruptedException e) {
                    }
                  }
                }
                m_code = 4;
                Dimension d = getSize();
                double lenX = 2 * d.width / 3 - 8 * m_currow  - 40;
                m_X = 2 * d.width / 3;
                double lenY = Math.abs((m_curstr - m_gram) * 14);
                m_Y = m_gram * 14 + 30;
                double dx = lenX / m_cntMove;
                double dy;
                if (m_curstr > m_gram) dy = - lenY / m_cntMove;
                else dy = lenY / m_cntMove;
                for (int j = 0; j < m_cntMove; j++) {
                  m_X -= dx;
                  m_Y -= dy;
                  repaint();
                  try {
                    analyser.sleep(ad);
                  }
                  catch (InterruptedException e) {
                  }
                }
              }
              if (syntesis.length() > 1) {
                m_code = 5;
                double lenX = (grammatic[m_gram][1].length() - 1) * 8;
                m_X = 20 + (m_currow + 1) * 8;
                double dx = lenX / 14;
                for (int j = 0; j < 14; j++) {
                  m_Y = (m_curstr - 1) * 14 + 30 + j;
                  repaint();
                  m_X += dx;
                  try {
                    analyser.sleep(ad);
                  }
                  catch (InterruptedException e) {
                  }
                }
              }
              m_code = 0;
              right = expression.substring(i + 1, expression.length());
							left = expression.substring(0, i);
              syntesis = Insert(syntesis, syntesis.indexOf('S') + 1, sign + "T");
							retArray = analysis(left, syntesis);
              expression = retArray[0];
              syntesis = retArray[1];
							p = false;
              break;
						}
		}
		if (p) {
      if (m_tut) {
        m_code = 2;
        for (int j = 0; j < m_cntFlash; j++) {
          m_gram = 2;
          if ((j % 2) == 0)
            m_flash = true;
          else
            m_flash = false;
          repaint();
          try {
            analyser.sleep(sd);
          }
          catch (InterruptedException e) {
          }
        }
        m_code = 3;
        if (m_currow > 0) {
          m_code = 3;
          for (int j = 0; j < 14; j++) {
            m_Y = (m_curstr - 1) * 14 + 30 + j;
            repaint();
            try {
              analyser.sleep(ad);
            }
            catch (InterruptedException e) {
            }
          }
        }
        m_code = 4;
        Dimension d = getSize();
        double lenX = 2 * d.width / 3 - 8 * m_currow  - 40;
        m_X = 2 * d.width / 3;
        double lenY = Math.abs((m_curstr - m_gram) * 14);
        m_Y = m_gram * 14 + 30;
        double dx = lenX / m_cntMove;
        double dy;
        if (m_curstr > m_gram) dy = - lenY / m_cntMove;
        else dy = lenY / m_cntMove;
        for (int j = 0; j < m_cntMove; j++) {
          m_X -= dx;
          m_Y -= dy;
          repaint();
          try {
            analyser.sleep(ad);
          }
          catch (InterruptedException e) {
          }
        }
        if (syntesis.length() > 1) {
          m_code = 5;
          lenX = (grammatic[m_gram][1].length() - 1) * 8;
          m_X = 20 + (m_currow + 1) * 8;
          dx = lenX / 14;
          for (int j = 0; j < 14; j++) {
            m_Y = (m_curstr - 1) * 14 + 30 + j;
            repaint();
            m_X += dx;
            try {
              analyser.sleep(ad);
            }
            catch (InterruptedException e) {
            }
          }
        }
      }
      m_code = 0;
			syntesis = SetAt(syntesis, syntesis.indexOf('S'), 'T');
			retArray = analysisT(left, syntesis);
      expression = retArray[0];
      syntesis = retArray[1];
      p = true;
			return retArray;
		}
		retArray = analysisT(right, syntesis);
    expression = retArray[0];
    syntesis = retArray[1];
		return retArray;
	}
  public String[] analysisT(String expression, String syntesis) {
		int skob = 0;
		char sign = 0;
		boolean p = true;
		String right = "",left = expression;
    String retArray[] = new String [2];

    syntes.addElement(syntesis);
    m_curstr++;
    if (m_curstr*14 > 300) {
      sb.setMaximum(sb.getMaximum() + 14);
    }
    if (m_tut) {
      m_code = 1; m_str = "T";
      for (int  j = 0; j < m_cntFlash; j++) {
        if ((j % 2) == 0)
          m_flash = true;
        else
          m_flash = false;
        repaint();
        try {
          analyser.sleep(sd);
        }
        catch (InterruptedException e) {
        }
      }
    }
    m_code = 0;
		for (int i = expression.length()-1; i >= 0; i--) {
			sign = expression.charAt(i);
			if (sign == '(') skob++;
			else
				if (sign == ')') skob--;
				else
					if (skob == 0)
						if (((sign == '*') || (sign == '/')) && i>0 && i<expression.length() - 1) {
              if (m_tut) {
                m_code = 2;
                for (int j = 0; j < m_cntFlash; j++) {
                  if (sign == '*')
                    m_gram = 3;
                  else
                    m_gram = 4;
                  if ((j % 2) == 0)
                    m_flash = true;
                  else
                    m_flash = false;
                  repaint();
                  try {
                    analyser.sleep(sd);
                  }
                  catch (InterruptedException e) {
                  }
                }
                m_code = 3;
                if (m_currow > 0) {
                  m_code = 3;
                  for (int j = 0; j < 14; j++) {
                    m_Y = (m_curstr - 1) * 14 + 30 + j;
                    repaint();
                    try {
                      analyser.sleep(ad);
                    }
                    catch (InterruptedException e) {
                    }
                  }
                }
                m_code = 4;
                Dimension d = getSize();
                double lenX = 2 * d.width / 3 - 8 * m_currow  - 40;
                m_X = 2 * d.width / 3;
                double lenY = Math.abs((m_curstr - m_gram) * 14);
                m_Y = m_gram * 14 + 30;
                double dx = lenX / m_cntMove;
                double dy;
                if (m_curstr > m_gram) dy = - lenY / m_cntMove;
                else dy = lenY / m_cntMove;
                for (int j = 0; j < m_cntMove; j++) {
                  m_X -= dx;
                  m_Y -= dy;
                  repaint();
                  try {
                    analyser.sleep(ad);
                  }
                  catch (InterruptedException e) {
                  }
                }
                if (syntesis.length() > 1) {
                  m_code = 5;
                  lenX = (grammatic[m_gram][1].length() - 1) * 8;
                  m_X = 20 + (m_currow + 1) * 8;
                  dx = lenX / 14;
                  for (int j = 0; j < 14; j++) {
                    m_Y = (m_curstr - 1) * 14 + 30 + j;
                    repaint();
                    m_X += dx;
                    try {
                      analyser.sleep(ad);
                    }
                    catch (InterruptedException e) {
                    }
                  }
                }
              }
              m_code = 0;
							right = expression.substring(i + 1, expression.length());
							left = expression.substring(0, i);
							syntesis = Insert(syntesis, syntesis.indexOf('T') + 1, sign + "F");
							retArray = analysisT(left, syntesis);
              expression = retArray[0];
              syntesis = retArray[1];
							p = false;
              break;
						}
		}
		if (p) {
      if (m_tut) {
        m_code = 2;
        for (int j = 0; j < m_cntFlash; j++) {
          m_gram = 5;
          if ((j % 2) == 0)
            m_flash = true;
          else
            m_flash = false;
          repaint();
          try {
            analyser.sleep(sd);
          }
          catch (InterruptedException e) {
          }
        }
        m_code = 3;
        if (m_currow > 0) {
          m_code = 3;
          for (int j = 0; j < 14; j++) {
            m_Y = (m_curstr - 1) * 14 + 30 + j;
            repaint();
            try {
              analyser.sleep(ad);
            }
            catch (InterruptedException e) {
            }
          }
        }
        m_code = 4;
        Dimension d = getSize();
        double lenX = 2 * d.width / 3 - 8 * m_currow  - 40;
        m_X = 2 * d.width / 3;
        double lenY = Math.abs((m_curstr - m_gram) * 14);
        m_Y = m_gram * 14 + 30;
        double dx = lenX / m_cntMove;
        double dy;
        if (m_curstr > m_gram) dy = - lenY / m_cntMove;
        else dy = lenY / m_cntMove;
        for (int j = 0; j < m_cntMove; j++) {
          m_X -= dx;
          m_Y -= dy;
          repaint();
          try {
            analyser.sleep(ad);
          }
          catch (InterruptedException e) {
          }
        }
        if (syntesis.length() > 1) {
          m_code = 5;
          lenX = (grammatic[m_gram][1].length() - 1) * 8;
          m_X = 20 + (m_currow + 1) * 8;
          dx = lenX / 14;
          for (int j = 0; j < 14; j++) {
            m_Y = (m_curstr - 1) * 14 + 30 + j;
            repaint();
            m_X += dx;
            try {
              analyser.sleep(ad);
            }
            catch (InterruptedException e) {
            }
          }
        }
      }
      m_code = 0;
			syntesis = SetAt(syntesis, syntesis.indexOf('T'), 'F');
			retArray = analysisF(left, syntesis);
      expression = retArray[0];
      syntesis = retArray[1];
      p = true;
			return retArray;
		}
		retArray = analysisF(right, syntesis);
    expression = retArray[0];
    syntesis = retArray[1];
		return retArray;
	}
  public String[] analysisF(String expression, String syntesis) {
		int skob = 0;
		char sign = 0;
		boolean p = true;
		String right = "", left = expression;
    String retArray[] = new String [2];

		syntes.addElement(syntesis);
    m_curstr++;
    if (m_curstr*14 > 300) {
      sb.setMaximum(sb.getMaximum() + 14);
    }
    if (m_tut) {
      m_code = 1; m_str = "F";
      for (int  j = 0; j < m_cntFlash; j++) {
        if ((j % 2) == 0)
          m_flash = true;
        else
          m_flash = false;
        repaint();
        try {
          analyser.sleep(sd);
        }
        catch (InterruptedException e) {
        }
      }
    }
    m_code = 0;
		for (int i = expression.length()-1; i >= 0; i--) {
			sign = expression.charAt(i);
			if (sign == ')') {
        if (m_tut) {
          m_code = 2;
          for (int j = 0; j < m_cntFlash; j++) {
            m_gram = 6;
            if ((j % 2) == 0)
              m_flash = true;
            else
              m_flash = false;
            repaint();
            try {
              analyser.sleep(sd);
            }
            catch (InterruptedException e) {
            }
          }
          m_code = 3;
          if (m_currow > 0) {
            m_code = 3;
            for (int j = 0; j < 14; j++) {
              m_Y = (m_curstr - 1) * 14 + 30 + j;
              repaint();
              try {
                analyser.sleep(ad);
              }
              catch (InterruptedException e) {
              }
            }
          }
          m_code = 4;
          Dimension d = getSize();
          double lenX = 2 * d.width / 3 - 8 * m_currow  - 40;
          m_X = 2 * d.width / 3;
          double lenY = Math.abs((m_curstr - m_gram) * 14);
          m_Y = m_gram * 14 + 30;
          double dx = lenX / m_cntMove;
          double dy;
          if (m_curstr > m_gram) dy = - lenY / m_cntMove;
          else dy = lenY / m_cntMove;
          for (int j = 0; j < m_cntMove; j++) {
            m_X -= dx;
            m_Y -= dy;
            repaint();
            try {
              analyser.sleep(ad);
            }
            catch (InterruptedException e) {
            }
          }
          if (syntesis.length() > 1) {
            m_code = 5;
            lenX = (grammatic[m_gram][1].length() - 1) * 8;
            m_X = 20 + (m_currow + 1) * 8;
            dx = lenX / 14;
            for (int j = 0; j < 14; j++) {
              m_Y = (m_curstr - 1) * 14 + 30 + j;
              repaint();
              m_X += dx;
              try {
                analyser.sleep(ad);
              }
              catch (InterruptedException e) {
              }
            }
          }
        }
        m_code = 0;
        m_currow++;
        int ent = syntesis.indexOf('F');
        syntesis = SetAt(syntesis, ent, sign);
        syntesis = Insert(syntesis, ent, "(S");
        expression = expression.substring(1, expression.length() - 1);
  			retArray = analysis(expression, syntesis);
        expression = retArray[0];
        syntesis = retArray[1];
        p = false;
        m_currow++;
        break;
      }
	  }
    if (p) {
      int f = syntesis.indexOf('F');
      if (m_tut) {
        m_code = 2;
        for (int j = 0; j < m_cntFlash; j++) {
          switch (expression.charAt(0)) {
            case 'a': { m_gram = 7; break; }
            case 'b': { m_gram = 8; break; }
            case 'c': { m_gram = 9; break; }
          }
          if ((j % 2) == 0)
            m_flash = true;
          else
            m_flash = false;
          repaint();
          try {
            analyser.sleep(sd);
          }
          catch (InterruptedException e) {
          }
        }
        m_code = 3;
          if (m_currow > 0) {
          m_code = 3;
          for (int j = 0; j < 14; j++) {
            m_Y = (m_curstr - 1) * 14 + 30 + j;
            repaint();
            try {
              analyser.sleep(ad);
            }
            catch (InterruptedException e) {
            }
          }
        }
        m_code = 4;
        Dimension d = getSize();
        double lenX = 2 * d.width / 3 - 8 * m_currow  - 40;
        m_X = 2 * d.width / 3;
        double lenY = Math.abs((m_curstr - m_gram) * 14);
        m_Y = m_gram * 14 + 30;
        double dx = lenX / m_cntMove;
        double dy;
        if (m_curstr > m_gram) dy = - lenY / m_cntMove;
        else dy = lenY / m_cntMove;
        for (int j = 0; j < m_cntMove; j++) {
          m_X -= dx;
          m_Y -= dy;
          repaint();
          try {
            analyser.sleep(ad);
          }
          catch (InterruptedException e) {
          }
        }
        if (syntesis.length() > 1) {
          m_code = 5;
          lenX = (grammatic[m_gram][1].length() - 1) * 8;
          m_X = 20 + (m_currow + 1) * 8;
          dx = lenX / 14;
          for (int j = 0; j < 14; j++) {
            m_Y = (m_curstr - 1) * 14 + 30 + j;
            repaint();
            m_X += dx;
            try {
              analyser.sleep(ad);
            }
            catch (InterruptedException e) {
            }
          }
        }
      }
      m_code = 0;
      m_currow += 2;
      syntesis = SetAt(syntesis, f, expression.charAt(0));
      retArray[0] = expression;
      retArray[1] = syntesis;
    }
	  return retArray;
	}
	public boolean syntax() {
    boolean r = true;
    if (expression.charAt(l) == 'a' || expression.charAt(l) == 'b' || expression.charAt(l) == 'c') {
      l++;
      if (l >= expression.length()) return r;
    }
    else {
      if (expression.charAt(l) == '(') {
        l++;
        if ((l >= expression.length())|| (!(r = syntax()))) return false;
        if (expression.charAt(l) != ')') {
          return false;
        }
        else {
          l++;
          if (l >= expression.length()) return r;
        }
      }
      else {
        return false;
      }
    }
    if (expression.charAt(l) == '+' || expression.charAt(l) == '-' || expression.charAt(l) == '/' || expression.charAt(l) == '*') {
      l++;
      if (l >= expression.length()) return false;
      if (!(r = syntax())) return false;
    }
    return r;
  }
  public void run() {
    while (true) {
      if (m_tut) {
        calcAnalysis();
        m_tut = false;
        parent.lb.setEnabled(true);
        analyser.suspend();
      }
    }
  }
  public void startAnalysis() {
    m_tut = true;
    if (analyser == null) {
      analyser = new Thread(this);
      analyser.start();
    }
    else {
      analyser.resume();
    }
  }
  public String Insert(String old, int idx, String sub) {
    return old.substring(0, idx) + sub + old.substring(idx);
  }
  public String SetAt(String old, int idx, char c) {
    return old.substring(0, idx) + c + old.substring(idx + 1);
  }
  public void adjustmentValueChanged(AdjustmentEvent ev) {
		offset = ev.getValue();
    repaint();
  }
}
///
class RightPanel extends Panel implements Runnable, AdjustmentListener {
  errorMessage err;
  protected Thread analyser = null;
	protected String[] parArray = new String[2];
	protected String syntesis = "S";
  protected String expression = "";
	protected Vector syntes = new Vector();
  public boolean m_tut = false, m_flash = false, m_b = false;
  protected String m_str;
  protected LRAnalysis parent = null;
  protected int sd, nd, ad, m_code, m_gram, m_curstr = 0, m_cntFlash = 6,
  m_currow = 0, m_cntMove = 10, offset = 0, l = 0;
  protected double m_X, m_Y;
  protected String[][] grammatic = { {"S->", "S+T"}, {"S->", "S-T"}, {"S->", "T"},
            {"T->", "T*F"}, {"T->", "T/F"}, {"T->", "F"}, {"F->", "(S)"},
            {"F->", "a"}, {"F->", "b"}, {"F->", "c"}};

  Scrollbar sb;
	public RightPanel(int sd, int ad, String expr, LRAnalysis parent) {
		this.sd = sd;
    this.ad = ad;
    this.parent = parent;
    this.expression = expr;
    setLayout(new BorderLayout());
    sb = new Scrollbar(Scrollbar.VERTICAL);
    sb.setMinimum(0);
    sb.setMaximum(0);
    sb.addAdjustmentListener(this);
    sb.setEnabled(true);
    add("East", sb);
    calcAnalysis();
	}

  Image offImage = null;
  Dimension offscreensize;
  Graphics offgraphics;

	public void paint(Graphics g) {
    update(g);
	}
  public synchronized void update(Graphics g) {
    Dimension d = getSize();
    Font fnt1 = new Font("Helvetica", Font.ITALIC, 12);
    Font fnt2 = new Font("Courier", Font.PLAIN, 13);
    Font fnt3 = new Font("Courier", Font.BOLD, 14);
    if ((offImage == null) || (d.width != offscreensize.width) || (d.height != offscreensize.height)) {
      offImage = createImage(d.width, d.height);
      offscreensize = d;
      offgraphics = offImage.getGraphics();
    }
//    offgraphics.setColor(new Color(163, 225, 250));
    offgraphics.setColor(new Color(Integer.parseInt(parent.bc)));
    offgraphics.fillRect(0, 0, d.width, d.height);
    offgraphics.setColor(Color.black);
    offgraphics.setFont(fnt1);
//    for (int i = 0; i < d.width / parent.background.getWidth(this) + 1; i++) {//
//      for (int j = 0; j < d.height / parent.background.getHeight(this) + 1; j++) {//
//        offgraphics.drawImage(parent.background, i * parent.background.getWidth(this), j * parent.background.getHeight(this), this);//
//      }//
//    }//
    offgraphics.drawString("Процес вывода выражения", 10, 15 - offset);
    offgraphics.drawString("Грамматика", 2 * d.width / 3, 15 - offset);
    offgraphics.setFont(fnt2);
    offgraphics.drawRect(0, 0, d.width  - 1, d.height - 1);
    for (int i = 0; i < grammatic.length; i++) {
      if (m_tut && m_code == 2 && m_gram == i) {}
      else
        offgraphics.drawString(grammatic[i][0] + grammatic[i][1], 2 * d.width / 3, 30 + i * 14 - offset);
    }
    if (m_tut == false) {
      for (int i = 0; i < syntes.size(); i++)
        offgraphics.drawString((String)syntes.elementAt(i), 20, 30 + 14 * i - offset);
    }
    else { // Tutorial
      for (int i = 0; i < m_curstr; i++) {
        offgraphics.drawString((String)syntes.elementAt(i), 20, 30 + 14 * i - offset);
      }
      if (m_currow > 0 && (m_code == 4 || m_code ==5)) {
        offgraphics.drawString(m_str, 20, 30 + 14 * m_curstr - offset);
      }
      switch (m_code) {
        case 1: {//Мигание правого нетерминала
          if (m_flash) {
            offgraphics.setFont(fnt3);
            offgraphics.setColor(Color.red);
          }
          else {
            offgraphics.setFont(fnt2);
          }
          offgraphics.drawString(m_str, 20 + 8 * m_currow, 30 + 14 * (m_curstr - 1) - offset);
          break;
        }
        case 2: {//Мигание порождающего правила
          offgraphics.drawString(grammatic[m_gram][0], 2 * d.width / 3, 30 + 14 * m_gram - offset);
          if (m_flash) {
            offgraphics.setFont(fnt3);
            offgraphics.setColor(Color.red);
          }
          else {
            offgraphics.setFont(fnt2);
          }
            offgraphics.drawString(grammatic[m_gram][1], 2 * d.width / 3 + grammatic[m_gram][0].length() * 8, 30 + 14 * m_gram - offset);
          break;
        }
        case 3: {//Движение полученых начала строки
          offgraphics.setColor(Color.green);
          offgraphics.drawString(m_str, 20, (int)m_Y - offset);
          break;
        }
        case 4: {//Движение правила
          offgraphics.setColor(Color.blue);
          offgraphics.drawString(grammatic[m_gram][1], (int)m_X, (int)m_Y - offset);
          break;
        }
        case 5: {//Движение остатка
          offgraphics.drawString(grammatic[m_gram][1], 20 + m_currow * 8, 30 + m_curstr * 14 - offset);
          offgraphics.setColor(Color.green);
          String s = syntes.elementAt(m_curstr - 1).toString();
          offgraphics.drawString(s.substring(m_currow + 1), (int)m_X, (int)m_Y - offset);
          break;
        }
      }
    }
    g.drawImage(offImage, 0, 0, null);
  }
	public void calcAnalysis() {
    l = 0;
 		if ((expression.length() + 1) % 2 == 0 && syntax()) {
			if (syntes.isEmpty() == false) {
				syntes.removeAllElements();
      }
      sb.setMaximum(0);
      m_curstr = 0;
      m_currow = 0;
      m_code = 0;
      m_b = false;
			parArray = analysis(expression, syntesis);
      syntes.addElement(parArray[1]);
      repaint();
      m_curstr++;
      if (m_tut) {
        repaint();
        try {
          analyser.sleep(sd);
        }
        catch (InterruptedException e) {
        }
      }
		}
    else {
      errorMessage err = new errorMessage(new Frame("Error!"), "Error.", true);
      err.resize(300, 100);
      err.show();
    }
	}
  public void start() {
    if (analyser != null)
      analyser.resume();
  }
  public void stop() {
    if (analyser != null)
      analyser.resume();
  }
	public String[] analysis(String expression, String syntesis) {
		int skob = 0;
		char sign = 0;
		boolean p = true;
		String right = expression, left = expression;
    String retArray[] = new String [2];

    syntes.addElement(syntesis);
    m_curstr++;
    if (m_curstr*14 > 300) {
      sb.setMaximum(sb.getMaximum() + 14);
    }
    if (m_tut) {
        m_code = 1; m_str = "S";
        for (int  j = 0; j < m_cntFlash; j++) {
        if ((j % 2) == 0)
          m_flash = true;
        else
          m_flash = false;
        repaint();
        try {
          analyser.sleep(sd);
        }
        catch (InterruptedException e) {
        }
      }
    }
    m_code = 0;
		for (int i = expression.length()-1; i >= 0; i--) {
			sign = expression.charAt(i);
			if (sign == '(') skob++;
			else
				if (sign == ')') skob--;
				else
					if (skob == 0)
						if (((sign == '+') || (sign == '-')) && i>0 && i<expression.length() - 1) {
              syntesis = Insert(syntesis, syntesis.lastIndexOf('S') + 1, sign + "T");
              if (m_tut) {
                m_code = 2;
                for (int j = 0; j < m_cntFlash; j++) {
                  if (sign == '+')
                    m_gram = 0;
                  else
                    m_gram = 1;
                  if ((j % 2) == 0)
                    m_flash = true;
                  else
                    m_flash = false;
                  repaint();
                  try {
                    analyser.sleep(sd);
                  }
                  catch (InterruptedException e) {
                  }
                }
                if (m_currow > 0) {
                  m_code = 3;
                  for (int j = 0; j < 14; j++) {
                    m_Y = (m_curstr - 1) * 14 + 30 + j;
                    m_str = syntesis.substring(0, m_currow);
                    repaint();
                    try {
                      analyser.sleep(ad);
                    }
                    catch (InterruptedException e) {
                    }
                  }
                }
                m_code = 4;
                Dimension d = getSize();
                double lenX = 2 * d.width / 3 - 8 * m_currow  - 40;
                m_X = 2 * d.width / 3;
                double lenY = Math.abs((m_curstr - m_gram) * 14);
                m_Y = m_gram * 14 + 30;
                double dx = lenX / m_cntMove;
                double dy;
                if (m_curstr > m_gram) dy = - lenY / m_cntMove;
                else dy = lenY / m_cntMove;
                for (int j = 0; j < m_cntMove; j++) {
                  m_X -= dx;
                  m_Y -= dy;
                  repaint();
                  try {
                    analyser.sleep(ad);
                  }
                  catch (InterruptedException e) {
                  }
                }
                if (m_b) {
                  m_code = 5;
                  lenX = (grammatic[m_gram][1].length() - 1) * 8;
                  m_X = 20 + (m_currow + 1) * 8;
                  dx = lenX / 14;
                  for (int j = 0; j < 14; j++) {
                    m_Y = (m_curstr - 1) * 14 + 30 + j;
                    repaint();
                    m_X += dx;
                    try {
                      analyser.sleep(ad);
                    }
                    catch (InterruptedException e) {
                    }
                  }
                }
              }
              m_code = 0;
              m_currow += 2;
              right = expression.substring(i + 1, expression.length());
							left = expression.substring(0, i);
							retArray = analysisT(right, syntesis);
              expression = retArray[0];
              syntesis = retArray[1];
							p = false;
              break;
						}
		}
		if (p) {
      if (m_tut) {
        m_code = 2;
        for (int j = 0; j < m_cntFlash; j++) {
          m_gram = 2;
          if ((j % 2) == 0)
            m_flash = true;
          else
            m_flash = false;
          repaint();
          try {
            analyser.sleep(sd);
          }
          catch (InterruptedException e) {
          }
        }
        if (m_currow > 0) {
          m_code = 3;
          for (int j = 0; j < 14; j++) {
            m_Y = (m_curstr - 1) * 14 + 30 + j;
            m_str = syntesis.substring(0, m_currow);
            repaint();
            try {
              analyser.sleep(ad);
            }
            catch (InterruptedException e) {
            }
          }
        }
        m_code = 4;
        Dimension d = getSize();
        double lenX = 2 * d.width / 3 - 8 * m_currow  - 40;
        m_X = 2 * d.width / 3;
        double lenY = Math.abs((m_curstr - m_gram) * 14);
        m_Y = m_gram * 14 + 30;
        double dx = lenX / m_cntMove;
        double dy;
        if (m_curstr > m_gram) dy = - lenY / m_cntMove;
        else dy = lenY / m_cntMove;
        for (int j = 0; j < m_cntMove; j++) {
          m_X -= dx;
          m_Y -= dy;
          repaint();
          try {
            analyser.sleep(ad);
          }
          catch (InterruptedException e) {
          }
        }
        if (m_b) {
          m_code = 5;
          lenX = (grammatic[m_gram][1].length() - 1) * 8;
          m_X = 20 + (m_currow + 1) * 8;
          dx = lenX / 14;
          for (int j = 0; j < 14; j++) {
            m_Y = (m_curstr - 1) * 14 + 30 + j;
            repaint();
            m_X += dx;
            try {
              analyser.sleep(ad);
            }
            catch (InterruptedException e) {
            }
          }
        }
      }
      m_code = 0;
			syntesis = SetAt(syntesis, syntesis.lastIndexOf('S'), 'T');
			retArray = analysisT(right, syntesis);
      expression = retArray[0];
      syntesis = retArray[1];
      p = true;
			return retArray;
		}
		retArray = analysis(left, syntesis);
    expression = retArray[0];
    syntesis = retArray[1];
		return retArray;
	}
  public String[] analysisT(String expression, String syntesis) {
		int skob = 0;
		char sign = 0;
		boolean p = true;
		String right = expression, left = expression;
    String retArray[] = new String [2];

    syntes.addElement(syntesis);
    m_curstr++;
    if (m_curstr*14 > 300) {
      sb.setMaximum(sb.getMaximum() + 14);
    }
    if (m_tut) {
      m_code = 1; m_str = "T";
      for (int  j = 0; j < m_cntFlash; j++) {
        if ((j % 2) == 0)
          m_flash = true;
        else
          m_flash = false;
        repaint();
        try {
          analyser.sleep(sd);
        }
        catch (InterruptedException e) {
        }
      }
    }
    m_code = 0;
		for (int i = expression.length()-1; i >= 0; i--) {
			sign = expression.charAt(i);
			if (sign == '(') skob++;
			else
				if (sign == ')') skob--;
				else
					if (skob == 0)
						if (((sign == '*') || (sign == '/')) && i>0 && i<expression.length() - 1) {
              syntesis = Insert(syntesis, syntesis.lastIndexOf('T') + 1, sign + "F");
              if (m_tut) {
                m_code = 2;
                for (int j = 0; j < m_cntFlash; j++) {
                  if (sign == '*')
                    m_gram = 3;
                  else
                    m_gram = 4;
                  if ((j % 2) == 0)
                    m_flash = true;
                  else
                    m_flash = false;
                  repaint();
                  try {
                    analyser.sleep(sd);
                  }
                  catch (InterruptedException e) {
                  }
                }
                if (m_currow > 0) {
                  m_code = 3;
                  for (int j = 0; j < 14; j++) {
                    m_Y = (m_curstr - 1) * 14 + 30 + j;
                    m_str = syntesis.substring(0, m_currow);
                    repaint();
                    try {
                      analyser.sleep(ad);
                    }
                    catch (InterruptedException e) {
                    }
                  }
                }
                m_code = 4;
                Dimension d = getSize();
                double lenX = 2 * d.width / 3 - 8 * m_currow  - 40;
                m_X = 2 * d.width / 3;
                double lenY = Math.abs((m_curstr - m_gram) * 14);
                m_Y = m_gram * 14 + 30;
                double dx = lenX / m_cntMove;
                double dy;
                if (m_curstr > m_gram) dy = - lenY / m_cntMove;
                else dy = lenY / m_cntMove;
                for (int j = 0; j < m_cntMove; j++) {
                  m_X -= dx;
                  m_Y -= dy;
                  repaint();
                  try {
                    analyser.sleep(ad);
                  }
                  catch (InterruptedException e) {
                  }
                }
                if (m_b) {
                  m_code = 5;
                  lenX = (grammatic[m_gram][1].length() - 1) * 8;
                  m_X = 20 + (m_currow + 1) * 8;
                  dx = lenX / 14;
                  for (int j = 0; j < 14; j++) {
                    m_Y = (m_curstr - 1) * 14 + 30 + j;
                    repaint();
                    m_X += dx;
                    try {
                      analyser.sleep(ad);
                    }
                    catch (InterruptedException e) {
                    }
                  }
                }
              }
              m_code = 0;
              m_currow += 2;
							right = expression.substring(i + 1, expression.length());
							left = expression.substring(0, i);
							retArray = analysisF(right, syntesis);
              expression = retArray[0];
              syntesis = retArray[1];
							p = false;
              break;
						}
		}
		if (p) {
      if (m_tut) {
        m_code = 2;
        for (int j = 0; j < m_cntFlash; j++) {
          m_gram = 5;
          if ((j % 2) == 0)
            m_flash = true;
          else
            m_flash = false;
          repaint();
          try {
            analyser.sleep(sd);
          }
          catch (InterruptedException e) {
          }
        }
        m_code = 3;
        if (m_currow > 0) {
          m_code = 3;
          for (int j = 0; j < 14; j++) {
            m_Y = (m_curstr - 1) * 14 + 30 + j;
            m_str = syntesis.substring(0, m_currow);
            repaint();
            try {
              analyser.sleep(ad);
            }
            catch (InterruptedException e) {
            }
          }
        }
        m_code = 4;
        Dimension d = getSize();
        double lenX = 2 * d.width / 3 - 8 * m_currow  - 40;
        m_X = 2 * d.width / 3;
        double lenY = Math.abs((m_curstr - m_gram) * 14);
        m_Y = m_gram * 14 + 30;
        double dx = lenX / m_cntMove;
        double dy;
        if (m_curstr > m_gram) dy = - lenY / m_cntMove;
        else dy = lenY / m_cntMove;
        for (int j = 0; j < m_cntMove; j++) {
          m_X -= dx;
          m_Y -= dy;
          repaint();
          try {
            analyser.sleep(ad);
          }
          catch (InterruptedException e) {
          }
        }
        if (m_b) {
          m_code = 5;
          lenX = (grammatic[m_gram][1].length() - 1) * 8;
          m_X = 20 + (m_currow + 1) * 8;
          dx = lenX / 14;
          for (int j = 0; j < 14; j++) {
            m_Y = (m_curstr - 1) * 14 + 30 + j;
            repaint();
            m_X += dx;
            try {
              analyser.sleep(ad);
            }
            catch (InterruptedException e) {
            }
          }
        }
      }
      m_code = 0;
			syntesis = SetAt(syntesis, syntesis.lastIndexOf('T'), 'F');
			retArray = analysisF(right, syntesis);
      expression = retArray[0];
      syntesis = retArray[1];
      p = true;
			return retArray;
		}
		retArray = analysisT(left, syntesis);
    expression = retArray[0];
    syntesis = retArray[1];
		return retArray;
	}
  public String[] analysisF(String expression, String syntesis) {
    Dimension d = getSize();
		int skob = 0;
		char sign = 0;
		boolean p = true;
    String retArray[] = new String [2];

		syntes.addElement(syntesis);
    m_curstr++;
    if (m_curstr*14 > 300) {
      sb.setMaximum(sb.getMaximum() + 14);
    }
    if (m_tut) {
      m_code = 1; m_str = "F";
      for (int  j = 0; j < m_cntFlash; j++) {
        if ((j % 2) == 0)
          m_flash = true;
        else
          m_flash = false;
        repaint();
        try {
          analyser.sleep(sd);
        }
        catch (InterruptedException e) {
        }
      }
    }
    m_code = 0;
		for (int i = expression.length()-1; i >= 0; i--) {
			sign = expression.charAt(i);
			if (sign == ')') {
        if (m_tut) {
          m_code = 2;
          for (int j = 0; j < m_cntFlash; j++) {
            m_gram = 6;
            if ((j % 2) == 0)
              m_flash = true;
            else
              m_flash = false;
            repaint();
            try {
              analyser.sleep(sd);
            }
            catch (InterruptedException e) {
            }
          }
          m_code = 3;
          if (m_currow > 0) {
            m_code = 3;
            for (int j = 0; j < 14; j++) {
              m_Y = (m_curstr - 1) * 14 + 30 + j;
              m_str = syntesis.substring(0, m_currow);
              repaint();
              try {
                analyser.sleep(ad);
              }
              catch (InterruptedException e) {
              }
            }
          }
          m_code = 4;
          d = getSize();
          double lenX = 2 * d.width / 3 - 8 * m_currow  - 40;
          m_X = 2 * d.width / 3;
          double lenY = Math.abs((m_curstr - m_gram) * 14);
          m_Y = m_gram * 14 + 30;
          double dx = lenX / m_cntMove;
          double dy;
          if (m_curstr > m_gram) dy = - lenY / m_cntMove;
          else dy = lenY / m_cntMove;
          for (int j = 0; j < m_cntMove; j++) {
            m_X -= dx;
            m_Y -= dy;
            repaint();
            try {
              analyser.sleep(ad);
            }
            catch (InterruptedException e) {
            }
          }
          if (m_b) {
            m_code = 5;
            lenX = (grammatic[m_gram][1].length() - 1) * 8;
            m_X = 20 + (m_currow + 1) * 8;
            dx = lenX / 14;
            for (int j = 0; j < 14; j++) {
              m_Y = (m_curstr - 1) * 14 + 30 + j;
              repaint();
              m_X += dx;
              try {
                analyser.sleep(ad);
              }
              catch (InterruptedException e) {
              }
            }
          }
        }
        m_code = 0;
        m_currow++;
        int ent = syntesis.indexOf('F');
        syntesis = SetAt(syntesis, ent, sign);
        syntesis = Insert(syntesis, ent, "(S");
        expression = expression.substring(1, expression.length() - 1);
  			retArray = analysis(expression, syntesis);
        expression = retArray[0];
        syntesis = retArray[1];
        p = false;
        m_currow --;
        break;
      }
	  }
    if (p) {
      int f = syntesis.indexOf('F');
      if (m_tut) {
        m_code = 2;
        for (int j = 0; j < m_cntFlash; j++) {
          switch (expression.charAt(0)) {
            case 'a': { m_gram = 7; break; }
            case 'b': { m_gram = 8; break; }
            case 'c': { m_gram = 9; break; }
          }
          if ((j % 2) == 0)
            m_flash = true;
          else
            m_flash = false;
          repaint();
          try {
            analyser.sleep(sd);
          }
          catch (InterruptedException e) {
          }
        }
        m_code = 3;
          if (m_currow > 0) {
          m_code = 3;
          for (int j = 0; j < 14; j++) {
            m_Y = (m_curstr - 1) * 14 + 30 + j;
            m_str = syntesis.substring(0, m_currow);
            repaint();
            try {
              analyser.sleep(ad);
            }
            catch (InterruptedException e) {
            }
          }
        }
        m_code = 4;
        d = getSize();
        double lenX = 2 * d.width / 3 - 8 * m_currow  - 40;
        m_X = 2 * d.width / 3;
        double lenY = Math.abs((m_curstr - m_gram) * 14);
        m_Y = m_gram * 14 + 30;
        double dx = lenX / m_cntMove;
        double dy;
        if (m_curstr > m_gram) dy = - lenY / m_cntMove;
        else dy = lenY / m_cntMove;
        for (int j = 0; j < m_cntMove; j++) {
          m_X -= dx;
          m_Y -= dy;
          repaint();
          try {
            analyser.sleep(ad);
          }
          catch (InterruptedException e) {
          }
        }
        if (m_b) {
          m_code = 5;
          lenX = (grammatic[m_gram][1].length() - 1) * 8;
          m_X = 20 + (m_currow + 1) * 8;
          dx = lenX / 14;
          for (int j = 0; j < 14; j++) {
            m_Y = (m_curstr - 1) * 14 + 30 + j;
            repaint();
            m_X += dx;
            try {
              analyser.sleep(ad);
            }
            catch (InterruptedException e) {
            }
          }
        }
      }
      m_code = 0;
      m_currow -= 2;
      m_b = true;
      syntesis = SetAt(syntesis, syntesis.lastIndexOf('F'), expression.charAt(0));
      retArray[0] = expression;
      retArray[1] = syntesis;
    }
	  return retArray;
	}
	public boolean syntax() {
    boolean r = true;
    if (expression.charAt(l) == 'a' || expression.charAt(l) == 'b' || expression.charAt(l) == 'c') {
      l++;
      if (l >= expression.length()) return r;
    }
    else {
      if (expression.charAt(l) == '(') {
        l++;
        if ((l >= expression.length())|| (!(r = syntax()))) return false;
        if (expression.charAt(l) != ')') {
          return false;
        }
        else {
          l++;
          if (l >= expression.length()) return r;
        }
      }
      else {
        return false;
      }
    }
    if (expression.charAt(l) == '+' || expression.charAt(l) == '-' || expression.charAt(l) == '/' || expression.charAt(l) == '*') {
      l++;
      if (l >= expression.length()) return false;
      if (!(r = syntax())) return false;
    }
    return r;
  }
  public void run() {
    while (true) {
      if (m_tut) {
        calcAnalysis();
        m_tut = false;
        parent.rb.setEnabled(true);
        analyser.suspend();
      }
    }
  }
  public void startAnalysis() {
    m_tut = true;
    if (analyser == null) {
      analyser = new Thread(this);
      analyser.start();
    }
    else {
      analyser.resume();
    }
  }
  public String Insert(String old, int idx, String sub) {
    return old.substring(0, idx) + sub + old.substring(idx);
  }
  public String SetAt(String old, int idx, char c) {
    return old.substring(0, idx) + c + old.substring(idx + 1);
  }
  public void adjustmentValueChanged(AdjustmentEvent ev) {
		offset = ev.getValue();
    repaint();
  }
}

