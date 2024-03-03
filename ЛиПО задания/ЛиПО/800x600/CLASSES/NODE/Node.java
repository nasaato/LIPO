package node;

public class Node {
  protected int level;
  protected int distance;
  protected String contents;
  protected Node parent;

  public Node() {
    this.level = 0;
    this.distance = 0;
    this.parent = null;
  }
  public Node(int level) {
    this.level = level;
  }
  public Node(int level, Node parent) {
    this(level);
    this.parent = parent;
  }
  public Node(int level, String contents) {
    this(level);
    this.contents = contents;
  }
  public Node(int level, Node parent, String contents) {
    this(level, contents);
    this.parent = parent;
  }
  public void setLevel(int level) {
    this.level = level;
  }
  public int getLevel() {
    return level;
  }
  public void setContents(String contents) {
    this.contents = contents;
  }
  public String getContents() {
    return contents;
  }
  public void setDistance(int distance) {
    this.distance = distance;
  }
  public int getDistance() {
    return distance;
  }
  public void setParent(Node parent) {
    this.parent = parent;
  }
  public Node getParent() {
    return parent;
  }
}