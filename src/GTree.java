/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author abdullah
 */

  import java.io.File;
public class GTree {
    private Node root;
    private Node currentNode;
    
    public GTree(File rootDirectory) {
        this.root = new Node(rootDirectory.getName(), rootDirectory, true);
        this.currentNode = root;
}

    public Node getRoot() {
        return root;
    }

    public Node getCurrentNode() {
        return currentNode;
    }

    public void setCurrentNode(Node currentNode) {
        this.currentNode = currentNode;
    }

    // Create a new node (folder or file)
    public Node createNode(String nodeName, boolean isDirectory) {
        File newFile = new File(currentNode.getFile(), nodeName);
        Node newNode = new Node(nodeName, newFile, isDirectory);

        if (isDirectory) {
            newFile.mkdir();
        } else {
            try {
                newFile.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        currentNode.addChild(newNode);
        return newNode;
    }

   
    public boolean deleteNode(Node nodeToDelete) {
        if (nodeToDelete != null) {
            if (nodeToDelete.isDirectory()) {
                File[] files = nodeToDelete.getFile().listFiles();
                if (files != null) {
                    for (File file : files) {
                        file.delete();
                    }
                }
            }
            nodeToDelete.getFile().delete();
            currentNode.removeChild(nodeToDelete);
            return true;
        }
        return false;
    }

    
    
public boolean renameNode(Node node, String newName) {
    if (node != null && node.getFile().getParentFile() != null) {
        File newFile = new File(node.getFile().getParentFile(), newName);
        if (!newFile.exists() && node.getFile().renameTo(newFile)) {
            node.setName(newName);
            node.setFile(newFile);
            return true;
        }
    }
    return false;
}


public void sortCurrentNodeChildren() {
    int childCount = currentNode.getChildCount();
    if (childCount > 1) {
        Node[] children = currentNode.getChildren();
        for (int i = 0; i < childCount - 1; i++) {
            for (int j = 0; j < childCount - i - 1; j++) {
                if (children[j].getName().compareToIgnoreCase(children[j + 1].getName()) > 0) {
                    // Swap nodes
                    Node temp = children[j];
                    children[j] = children[j + 1];
                    children[j + 1] = temp;
                }
            }
        }
    }
}


   
    public Node searchNode(String nodeName) {
        return searchNodeRecursive(currentNode, nodeName);
    }

    private Node searchNodeRecursive(Node node, String nodeName) {
        if (node.getName().equalsIgnoreCase(nodeName)) {
            return node;
        }
        for (int i = 0; i < node.getChildCount(); i++) {
            Node foundNode = searchNodeRecursive(node.getChildren()[i], nodeName);
            if (foundNode != null) {
                return foundNode;
            }
        }
        return null;
    }

    
    public void navigateTo(Node node) {
        currentNode = node;
    }
}

    

