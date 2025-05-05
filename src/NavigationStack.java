import java.io.File;

public class NavigationStack {
    private int size;
    private File[] stackA;
    private int ptr;

    
    public NavigationStack(int size) {
        stackA = new File[size];
        ptr = -1;  
    }

    
    public void push(File file) {
        if (ptr == stackA.length - 1) {
            resize();  
        }
        stackA[++ptr] = file;  
    }

    
    public File pop() {
        if (isEmpty()) {
            return null;  
        }
        return stackA[ptr--];  
    }

    
    public boolean isEmpty() {
        return ptr == -1;  
    }

    
    private void resize() {
        File[] newStack = new File[stackA.length * 2];  
        System.arraycopy(stackA, 0, newStack, 0, stackA.length);  
        stackA = newStack;  
    }

    
    public File peek() {
        if (isEmpty()) {
            return null;
        }
        return stackA[ptr];  
    }
}
