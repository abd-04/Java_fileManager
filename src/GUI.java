

import javax.swing.*;
import java.awt.*;
import java.io.*;

public class GUI {
    private static GTree fileTree;

    public static void main(String[] args) {
        File rootDirectory = new File("D://myRoot");
        fileTree = new GTree(rootDirectory);

        JFrame frame = new JFrame("File Explorer");
        frame.setSize(800, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JSplitPane splitPane = new JSplitPane();
        splitPane.setDividerLocation(300);
        splitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setBackground(Color.yellow);

        DefaultListModel<String> listModel = new DefaultListModel<>();
        JList<String> directoryList = new JList<>(listModel);
        JScrollPane listScrollPane = new JScrollPane(directoryList);
        splitPane.setLeftComponent(listScrollPane);

        String[] columnNames = {"Name", "Type", "Size"};
        Object[][] data = {}; // Initially empty
        JTable fileTable = new JTable(data, columnNames);
        JScrollPane tableScrollPane = new JScrollPane(fileTable);
        splitPane.setRightComponent(tableScrollPane);

        frame.add(splitPane, BorderLayout.CENTER);

        JButton backButton = new JButton("Back");
        JButton forwardButton = new JButton("Forward");
        backButton.setBackground(Color.cyan);
        forwardButton.setBackground(Color.PINK);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(backButton);
        buttonPanel.add(forwardButton);
        frame.add(buttonPanel, BorderLayout.NORTH);

        frame.setVisible(true);

        populateDirectoryList(listModel, rootDirectory);
        directoryList.setBackground(Color.YELLOW);

       directoryList.addListSelectionListener(e -> {
    if (!e.getValueIsAdjusting()) {
        String selectedDirectoryName = directoryList.getSelectedValue(); // Likely causing the issue
        File selectedDirectory = new File(fileTree.getCurrentNode().getFile(), selectedDirectoryName);
        if (selectedDirectory.isDirectory()) {
            fileTree.navigateTo(new Node(selectedDirectory.getName(), selectedDirectory, true));
            displayFiles(fileTree.getCurrentNode().getFile(), fileTable);
            populateDirectoryList(listModel, fileTree.getCurrentNode().getFile());
        }
    }
});

        JButton createButton = new JButton("Create Node");
        JButton deleteButton = new JButton("Delete Node");
        JButton searchButton = new JButton("Search Node");
        JButton renameButton = new JButton("Rename");
        JButton sortButton = new JButton("Sort");
        JButton editButton = new JButton("Edit Text File");

        buttonPanel.add(createButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(searchButton);
      //  buttonPanel.add(renameButton);
        buttonPanel.add(sortButton);
        buttonPanel.add(editButton);

        // Action listener for renaming nodes
        renameButton.addActionListener(e -> {
            String selectedNodeName = directoryList.getSelectedValue();
            if (selectedNodeName != null) {
                Node selectedNode = fileTree.searchNode(selectedNodeName);
                if (selectedNode != null) {
                    String newName = JOptionPane.showInputDialog(frame, "Enter new name:", selectedNode.getName());
                    if (newName != null && !newName.trim().isEmpty()) {
                        boolean renamed = fileTree.renameNode(selectedNode, newName.trim());
                        if (renamed) {
                            JOptionPane.showMessageDialog(frame, "Renamed successfully!");
                            populateDirectoryList(listModel, fileTree.getCurrentNode().getFile());
                            displayFiles(fileTree.getCurrentNode().getFile(), fileTable);
                        } else {
                            JOptionPane.showMessageDialog(frame, "Rename failed. File/folder may already exist.");
                        }
                    } else {
                        JOptionPane.showMessageDialog(frame, "Invalid name. Please try again.");
                    }
                } else {
                    JOptionPane.showMessageDialog(frame, "Node not found. Please try again.");
                }
            } else {
                JOptionPane.showMessageDialog(frame, "No node selected. Please select a node to rename.");
            }
        });

        // Action listener for sorting nodes
        sortButton.addActionListener(e -> {
            fileTree.sortCurrentNodeChildren();
            populateDirectoryList(listModel, fileTree.getCurrentNode().getFile());
            displayFiles(fileTree.getCurrentNode().getFile(), fileTable);
            JOptionPane.showMessageDialog(frame, "Nodes sorted alphabetically!");
        });

        // Action listener for editing text files
        editButton.addActionListener(e -> {
            String selectedNodeName = directoryList.getSelectedValue();
            if (selectedNodeName != null) {
                File selectedFile = new File(fileTree.getCurrentNode().getFile(), selectedNodeName);
                if (selectedFile.isFile() && selectedFile.getName().endsWith(".txt")) {
                    editTextFile(selectedFile);
                } else {
                    JOptionPane.showMessageDialog(frame, "Please select a valid text file.");
                }
            } else {
                JOptionPane.showMessageDialog(frame, "No file selected. Please select a file to edit.");
            }
        });

        createButton.addActionListener(e -> {
            createNewNode();
            populateDirectoryList(listModel, fileTree.getCurrentNode().getFile());
        });

        deleteButton.addActionListener(e -> {
            deleteNode();
            populateDirectoryList(listModel, fileTree.getCurrentNode().getFile());
        });

        searchButton.addActionListener(e -> {
            searchNode();
            populateDirectoryList(listModel, fileTree.getCurrentNode().getFile());
        });
    }

    private static void populateDirectoryList(DefaultListModel<String> listModel, File currentDirectory) {
        File[] files = currentDirectory.listFiles();
        if (files != null) {
            listModel.clear();
            for (File file : files) {
                listModel.addElement(file.getName());
            }
        }
    }

    private static void displayFiles(File currentDirectory, JTable fileTable) {
        File[] files = currentDirectory.listFiles();
        if (files != null) {
            String[][] fileData = new String[files.length][3];
            for (int i = 0; i < files.length; i++) {
                fileData[i][0] = files[i].getName();
                fileData[i][1] = files[i].isDirectory() ? "Folder" : "File";
                fileData[i][2] = String.valueOf(files[i].length()) + " bytes";
            }
            fileTable.setModel(new javax.swing.table.DefaultTableModel(fileData, new String[]{"Name", "Type", "Size"}));
        }
    }

    private static void createNewNode() {
        String[] options = {"Folder", "Text File"};
        int choice = JOptionPane.showOptionDialog(
            null,
            "Choose the type of node to create",
            "Create New Node",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.INFORMATION_MESSAGE,
            null,
            options,
            options[0]
        );

        String nodeName = JOptionPane.showInputDialog("Enter the name of the new node:");
        if (nodeName != null && !nodeName.trim().isEmpty()) {
            boolean isDirectory = (choice == 0);

            try {
                File newNode;
                if (isDirectory) {
                    newNode = new File(fileTree.getCurrentNode().getFile(), nodeName);
                    if (newNode.mkdir()) {
                        JOptionPane.showMessageDialog(null, "Folder created successfully!");
                    } else {
                        JOptionPane.showMessageDialog(null, "Failed to create folder. It might already exist.");
                    }
                } else {
                    if (!nodeName.endsWith(".txt")) {
                        nodeName += ".txt";
                    }
                    newNode = new File(fileTree.getCurrentNode().getFile(), nodeName);
                    if (newNode.createNewFile()) {
                        JOptionPane.showMessageDialog(null, "Text file created successfully!");
                        try (FileWriter writer = new FileWriter(newNode)) {
                            writer.write("Initial content");
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Failed to create text file. It might already exist.");
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Error creating node: " + ex.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(null, "Invalid name. Please try again.");
        }
    }

   private static void deleteNode() {
    String nodeName = JOptionPane.showInputDialog("Enter the name of the node (folder or file) to delete:");
    if (nodeName != null && !nodeName.trim().isEmpty()) {
        Node nodeToDelete = fileTree.searchNode(nodeName); // Searches from the root
        if (nodeToDelete != null) {
            int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this node?", "Delete Node", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                boolean success = fileTree.deleteNode(nodeToDelete);
                if (success) {
                    JOptionPane.showMessageDialog(null, "Node deleted successfully!");
                } else {
                    JOptionPane.showMessageDialog(null, "Failed to delete node.");
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "Node not found.");
        }
    }
}


    private static void searchNode() {
    String nodeName = JOptionPane.showInputDialog("Enter the name of the node to search:");
    if (nodeName != null && !nodeName.trim().isEmpty()) {
        Node foundNode = fileTree.searchNode(nodeName); // Searches from the root
        if (foundNode != null) {
            JOptionPane.showMessageDialog(null, "Node found: " + foundNode.getFile().getAbsolutePath());
        } else {
            JOptionPane.showMessageDialog(null, "Node not found.");
        }
    }
}


    private static void editTextFile(File textFile) {
        if (textFile != null && textFile.isFile() && textFile.getName().endsWith(".txt")) {
            try {
                String currentContent = new String(java.nio.file.Files.readAllBytes(textFile.toPath()));

                JTextArea textArea = new JTextArea(20, 40);
                textArea.setText(currentContent);
                int result = JOptionPane.showConfirmDialog(
                    null, new JScrollPane(textArea),
                    "Edit File: " + textFile.getName(),
                    JOptionPane.OK_CANCEL_OPTION,
                    JOptionPane.PLAIN_MESSAGE
                );

                if (result == JOptionPane.OK_OPTION) {
                    try (FileWriter writer = new FileWriter(textFile)) {
                        writer.write(textArea.getText());
                    }
                    JOptionPane.showMessageDialog(null, "File updated successfully!");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Error editing file: " + ex.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(null, "Invalid text file selected.");
        }
    }
}
