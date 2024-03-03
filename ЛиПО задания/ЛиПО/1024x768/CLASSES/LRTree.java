import java.awt.*;
import java.awt.event.*;
import java.applet.*;
import java.util.Vector;
import error.*;
import node.*;

public class LRTree extends Applet implements ActionListener {
	TextField src;
	String szFontName = "TimesRoman";
  Button lb;
  LTreePanel lp;
  String bc;

	public String getAppletInfo() {
		return "Name: LeftTree Analyser";
	}
	public void init() {
    bc = getParameter("gnd");
    if (bc == null) bc = "10740218";
		int sd = 250;
    setLayout(new BorderLayout());
    Panel p = new Panel();
    p.setLayout(new GridLayout(0, 1, 10, 10));
//    p.setBackground(new Color(163, 225, 250));
    p.setBackground(new Color(Integer.parseInt(bc)));
    lp = new LTreePanel(sd, "(a+b)*c", this);
    p.add(lp);
    add("Center", p);
    Panel control = new Panel();
    add("North", control);
		lb = new Button("Дерево разбора");
		lb.addActionListener(this);
    control.add("South", lb);
		control.add(src = new TextField("(a+b)*c", 21));
    control.setBackground(new Color(Integer.parseInt(bc)));
	}
	public void destroy() {
	}
	public static void main(String args[]) {
		Frame f = new Frame("LeftSide and RightSide Analysis");
		LRTree lra = new LRTree();
    lra.init();
//		lra.start();
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
  	lp.expression = src.getText();
    lb.setEnabled(false);
    lp.startAnalysis();
  }
}
///
class LTreePanel extends Panel implements Runnable, AdjustmentListener {
  errorMessage err;
  protected Thread analyser = null;
	protected String[] parArray = new String[2];
	protected String syntesis = "S";
  protected String expression = "";
	protected Vector tree = new Vector();
  public boolean m_tut = false, m_flash = false;
  protected Node root = new Node(0);
  protected LRTree parent = null;
  protected int sd, m_code, m_maxl, m_curstr = 0,
  m_currow = 0, m_cntMove = 10, offset = 0, slide = 0, l = 0, paragraph;
  protected double m_X, m_Y;
  Scrollbar vsb, hsb;

	public LTreePanel(int sd, String expr, LRTree parent) {
		this.sd = sd;
    this.parent = parent;
    this.expression = expr;
    setLayout(new BorderLayout());
    vsb = new Scrollbar(Scrollbar.VERTICAL);
    vsb.setMaximum(500);
    vsb.addAdjustmentListener(this);
    vsb.setEnabled(true);
    hsb = new Scrollbar(Scrollbar.HORIZONTAL);
    hsb.setMaximum(500);
    hsb.addAdjustmentListener(this);
    hsb.setEnabled(true);
    add("East", vsb);
    add("South", hsb);
    calcAnalysis();
	}

  Image offImage = null;
  Dimension offscreensize;
  Graphics offgraphics;

	public void paint(Graphics g) {
    update(g);
	}
  public void showAnalysis() {
    l = 0;
    m_code = 1;
    for (m_X = 0; m_X < m_cntMove; m_X += 3) {
      repaint();
      try {
        analyser.sleep(sd);
      }
      catch (InterruptedException e) {
      }
    }
    for (l = 1; l < m_maxl; l++) {
      m_code = 2;
      for (m_X = 0; m_X < m_cntMove; m_X += 3) {
        repaint();
        try {
          analyser.sleep(sd);
        }
        catch (InterruptedException e) {
        }
      }
      m_code = 1;
      for (m_X = 0; m_X < m_cntMove; m_X += 3) {
        repaint();
        try {
          analyser.sleep(sd);
        }
        catch (InterruptedException e) {
        }
      }
    }
    m_code = 0;
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
    offgraphics.setColor(new Color(Integer.parseInt(parent.bc)));
    offgraphics.fillRect(0, 0, d.width, d.height);
    offgraphics.setColor(Color.black);
    offgraphics.setFont(fnt1);
    offgraphics.setFont(fnt2);
    offgraphics.drawRect(0, 0, d.width  - 1, d.height - 1);
    if (m_tut == false) {
      for (int i = 0; i < tree.size(); i++) {
        Node n  = new Node();
        n = (Node)tree.elementAt(i);
        Node p = new Node();
        if (n.getParent() != null) {
          p = n.getParent();
        }
        int x = 20 + n.getDistance() * 7 - slide;
        int y = 30 + 28 * n.getLevel() - offset;
        int px = 20 + p.getDistance() * 7 - slide;
        int py = 30 + 28 * p.getLevel() - offset;
        offgraphics.drawOval(x - 4, y - 11, 16, 16);
        offgraphics.drawString(n.getContents(), x, y);
        if (n.getParent() != null) {
          offgraphics.drawLine(x + 4, y - 12, px + 4, py + 5);
        }
      }
    }
    else { // Tutorial
      for (int i = 0; i < tree.size(); i++) {
        Node n  = new Node();
        n = (Node)tree.elementAt(i);
        if (n.getLevel() < l) {
          Node p = new Node();
          if (n.getParent() != null) {
            p = n.getParent();
          }
          int x = 20 + n.getDistance() * 7 - slide;
          int y = 30 + 28 * n.getLevel() - offset;
          int px = 20 + p.getDistance() * 7 - slide;
          int py = 30 + 28 * p.getLevel() - offset;
          offgraphics.drawOval(x - 4, y - 11, 16, 16);
          offgraphics.drawString(n.getContents(), x, y);
          if (n.getParent() != null) {
            offgraphics.drawLine(x + 4, y - 12, px + 4, py + 5);
          }
        }
      }
      switch (m_code) {
        case 1: {
          for (int i = 0; i < tree.size(); i++) {
            Node n  = new Node();
            n = (Node)tree.elementAt(i);
            if (n.getLevel() == l) {
              Node p = new Node();
              if (n.getParent() != null) {
                p = n.getParent();
              }
              int x = 20 + n.getDistance() * 7 - slide;
              int y = 30 + 28 * n.getLevel() - offset;
              int px = 20 + p.getDistance() * 7 - slide;
              int py = 30 + 28 * p.getLevel() - offset;
              if (n.getParent() != null) {
                offgraphics.drawLine(x + 4, y - 12, px + 4, py + 5);
              }
              offgraphics.drawOval(x - 4, y - 11, (int)m_X, (int)m_X);
            }
          }
          break;
        }
        case 2: {
          for (int i = 0; i < tree.size(); i++) {
            Node n  = new Node();
            n = (Node)tree.elementAt(i);
            if (n.getLevel() == l) {
              Node p = new Node();
              if (n.getParent() != null) {
                p = n.getParent();
              }
              int x = 20 + n.getDistance() * 7 - slide;
              int y = 30 + 28 * n.getLevel() - offset;
              int px = 20 + p.getDistance() * 7 - slide;
              int py = 30 + 28 * p.getLevel() - offset;
              if (n.getParent() != null) {
                offgraphics.drawLine(x + (int)m_X - 6, y + (int)m_X - 22, px + 4, py + 5);
              }
            }
          }
          break;
        }
      }
    }
    g.drawImage(offImage, 0, 0, null);
  }
	public boolean calcAnalysis() {
    l = 0;
		if ((expression.length() + 1) % 2 == 0 && syntax()) {
			if (tree.isEmpty() == false) {
				tree.removeAllElements();
      }
      m_curstr = 0;
      m_currow = 0;
      paragraph = 30;
      root.setContents("S");
      root.setLevel(0);
      root.setDistance(paragraph);
      tree.addElement(root);
			parArray = analysis(expression, syntesis, root);
      repaint();
      m_curstr++;
      for (int i = 0; i < tree.size(); i++) {
        Node n = new Node();
        n = (Node)tree.elementAt(i);
        if (m_maxl < n.getLevel()) m_maxl = n.getLevel();
      }
		}
    else {
      errorMessage err = new errorMessage(new Frame("Error!"), "Error.", true);
      err.resize(300, 100);
      err.show();
      return false;
    }
    return true;
	}
  public void start() {
    if (analyser != null)
      analyser.resume();
  }
  public void stop() {
    if (analyser != null)
      analyser.suspend();
  }
	public String[] analysis(String expression, String syntesis, Node parent) {
		int skob = 0, parmax = 0;
		char sign = 0;
		boolean p = true;
		String right = "",left = expression;
    String retArray[] = new String [2];
    Node nodes[] = new Node[3];
    Node nmax = new Node();

    m_curstr++;
    if (m_tut) {
    }
		for (int i = expression.length()-1; i >= 0; i--) {
			sign = expression.charAt(i);
			if (sign == '(') skob++;
			else
				if (sign == ')') skob--;
				else
					if (skob == 0)
						if (((sign == '+') || (sign == '-')) && i>0 && i<expression.length() - 1) {
              right = expression.substring(i + 1, expression.length());
							left = expression.substring(0, i);
              syntesis = Insert(syntesis, syntesis.indexOf('S') + 1, sign + "T");
              for (int n = 0; n < 3; n++) {
                nodes[n] = new Node(m_curstr, parent);
              }
              for (int n = 0; n < tree.size(); n++) {
                nmax = (Node)tree.elementAt(n);
                if ((nmax.getDistance() > parmax) && (nmax.getLevel() == m_curstr)) {
                  parmax = nmax.getDistance();
                }
              }
              if ((paragraph - 3) > parmax) {
                paragraph -= 3;
              }
              else {
                paragraph = parmax + 3;
              }
              nodes[0].setContents("S");
              nodes[0].setDistance(paragraph);
              tree.addElement(nodes[0]);
							retArray = analysis(left, syntesis, nodes[0]);
              expression = retArray[0];
              syntesis = retArray[1];
              nodes[1].setContents("+");
              paragraph += 3;
              nodes[1].setDistance(paragraph);
              tree.addElement(nodes[1]);
							p = false;
              break;
						}
		}
		if (p) {
      Node node = new Node(m_curstr, parent, "T");
      node.setDistance(paragraph);
      tree.addElement(node);
			syntesis = SetAt(syntesis, syntesis.indexOf('S'), 'T');
			retArray = analysisT(left, syntesis, node);
      expression = retArray[0];
      syntesis = retArray[1];
      p = true;
      m_curstr--;
			return retArray;
		}
    paragraph += 3;
    Node node = new Node();
    node = nodes[2];
    node.setDistance(paragraph);
    node.setContents("T");
    tree.addElement(node);
		retArray = analysisT(right, syntesis, node);
    expression = retArray[0];
    syntesis = retArray[1];
    m_curstr--;
		return retArray;
	}
  public String[] analysisT(String expression, String syntesis, Node parent) {
		int skob = 0, parmax = 0;
		char sign = 0;
		boolean p = true;
		String right = "",left = expression;
    String retArray[] = new String [2];
    Node nodes[] = new Node[3];
    Node nmax = new Node();

    m_curstr++;
		for (int i = expression.length()-1; i >= 0; i--) {
			sign = expression.charAt(i);
			if (sign == '(') skob++;
			else
				if (sign == ')') skob--;
				else
					if (skob == 0)
						if (((sign == '*') || (sign == '/')) && i>0 && i<expression.length() - 1) {
              for (int n = 0; n < 3; n++) {
                nodes[n] = new Node(m_curstr, parent);
              }
              for (int n = 0; n < tree.size(); n++) {
                nmax = (Node)tree.elementAt(n);
                if ((nmax.getDistance() > parmax) && (nmax.getLevel() == m_curstr)) {
                  parmax = nmax.getDistance();
                }
              }
              if ((paragraph - 3) > parmax) {
                paragraph -= 3;
              }
              else {
                paragraph = parmax + 3;
              }
              nodes[0].setContents("T");
              nodes[0].setDistance(paragraph);
              tree.addElement(nodes[0]);

							right = expression.substring(i + 1, expression.length());
							left = expression.substring(0, i);
              syntesis = Insert(syntesis, syntesis.indexOf('T') + 1, sign + "F");
							retArray = analysisT(left, syntesis, nodes[0]);
              expression = retArray[0];
              syntesis = retArray[1];
              paragraph += 3;
              nodes[1].setContents("*");
              nodes[1].setDistance(paragraph);
              tree.addElement(nodes[1]);
							p = false;
              break;
						}
		}
		if (p) {
      Node node = new Node(m_curstr, parent, "F");
      node.setDistance(parent.getDistance());
      tree.addElement(node);
			syntesis = SetAt(syntesis, syntesis.indexOf('T'), 'F');
			retArray = analysisF(left, syntesis, node);
      expression = retArray[0];
      syntesis = retArray[1];
      p = true;
      m_curstr--;
			return retArray;
		}
    Node node = new Node();
    node = nodes[2];
    paragraph += 3;
    node.setDistance(paragraph);
    node.setContents("F");
    tree.addElement(node);
		retArray = analysisF(right, syntesis, node);
    expression = retArray[0];
    syntesis = retArray[1];
    m_curstr--;
		return retArray;
	}
  public String[] analysisF(String expression, String syntesis, Node parent) {
		int skob = 0, parmax = 0;
		char sign = 0;
		boolean p = true;
		String right = "", left = expression;
    String retArray[] = new String [2];
    Node nodes[] = new Node[3];
    Node nmax = new Node();

    m_curstr++;
		for (int i = expression.length()-1; i >= 0; i--) {
			sign = expression.charAt(i);
			if (sign == ')') {
        for (int n = 0; n < 3; n++) {
          nodes[n] = new Node(m_curstr, parent);
        }
        for (int n = 0; n < tree.size(); n++) {
          nmax = (Node)tree.elementAt(n);
          if ((nmax.getDistance() > parmax) && (nmax.getLevel() == m_curstr)) {
            parmax = nmax.getDistance();
          }
        }
        if ((paragraph - 3) > parmax) {
          paragraph -= 3;
        }
        else {
          paragraph = parmax + 3;
        }
        nodes[0].setContents("(");
        nodes[0].setDistance(paragraph);
        tree.addElement(nodes[0]);
        paragraph += 3;
        nodes[1].setContents("S");
        nodes[1].setDistance(paragraph);
        tree.addElement(nodes[1]);
        int ent = syntesis.indexOf('F');
        syntesis = SetAt(syntesis, ent, sign);
        syntesis = Insert(syntesis, ent, "(S");
        expression = expression.substring(1, expression.length() - 1);
  			retArray = analysis(expression, syntesis, nodes[1]);
        expression = retArray[0];
        syntesis = retArray[1];
        nodes[2].setContents(")");
        nodes[2].setDistance(paragraph);
        tree.addElement(nodes[2]);
        p = false;
        m_currow++;
        break;
      }
	  }
    if (p) {
      Node node = new Node(m_curstr, parent, expression);
      node.setDistance(parent.getDistance());
      tree.addElement(node);
      int f = syntesis.indexOf('F');
      m_currow += 2;
      syntesis = SetAt(syntesis, f, expression.charAt(0));
      retArray[0] = expression;
      retArray[1] = syntesis;
    }
    m_curstr--;
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
        showAnalysis();
        m_tut = false;
        parent.lb.setEnabled(true);
        repaint();
        analyser.suspend();
      }
    }
  }
  public void startAnalysis() {
    if (calcAnalysis()) {
      m_tut = true;
      if (analyser == null) {
        analyser = new Thread(this);
        analyser.start();
      }
      else {
        analyser.resume();
      }
    }
    else {
      parent.lb.setEnabled(true);
    }
  }
  public String Insert(String old, int idx, String sub) {
    return old.substring(0, idx) + sub + old.substring(idx);
  }
  public String SetAt(String old, int idx, char c) {
    return old.substring(0, idx) + c + old.substring(idx + 1);
  }
  public void adjustmentValueChanged(AdjustmentEvent ev) {
    Scrollbar s = new Scrollbar();
    s = (Scrollbar)ev.getSource();
    if (s.getOrientation() == Scrollbar.VERTICAL) {
  		offset = ev.getValue();
      repaint();
    }
    else {
      slide = ev.getValue();
      repaint();
    }
  }
}

