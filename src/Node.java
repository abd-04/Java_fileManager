





import java.io.File;

public class Node {
    private String name;
    private File file;
    private boolean isDirectory;
    private Node[] children;
    private int childCount;

    // Constructor for Node
    public Node(String name, File file, boolean isDirectory) {
        this.name = name;
        this.file = file;
        this.isDirectory = isDirectory;
        this.children = new Node[10];  // Initial capacity for children
        this.childCount = 0;
    }

    public String getName() {
        return name;
    }

    // Add setters for name and file
public void setName(String name) {
    this.name = name;
}

public void setFile(File file) {
    this.file = file;
}

    
    public File getFile() {
        return file;
    }

    public boolean isDirectory() {
        return isDirectory;
    }

    public Node[] getChildren() {
        return children;
    }

    public int getChildCount() {
        return childCount;
    }

    public void addChild(Node child) {
        if (childCount < children.length) {
            children[childCount++] = child;
        } else {
            expandChildren();
            children[childCount++] = child;
        }
    }

    public void removeChild(Node child) {
        for (int i = 0; i < childCount; i++) {
            if (children[i] == child) {
                for (int j = i; j < childCount - 1; j++) {
                    children[j] = children[j + 1];
                }
                children[--childCount] = null;
                break;
            }
        }
    }

    private void expandChildren() {
        Node[] newChildren = new Node[children.length * 2];
        System.arraycopy(children, 0, newChildren, 0, children.length);
        children = newChildren;
    }
}
