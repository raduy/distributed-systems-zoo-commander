package pl.agh.sr.zookeeper.visitor;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Lukasz Raduj 2015 raduj.lukasz@gmail.com.
 */
public class TreePrintingChildVisitor implements ChildVisitor {

    private final static String DEFAULT_INIT_STRING = "";
    private final static String DEFAULT_INTENT = "---";
    private final StringBuilder builder;
    private final String intent;
    private String currentIntent = "";

    public TreePrintingChildVisitor(String intent, String initString) {
        checkNotNull(intent, "Intent cannot be null!");
        checkNotNull(initString, "Init string cannot be null");
        this.intent = intent;
        this.builder = new StringBuilder(initString);
    }

    public TreePrintingChildVisitor() {
        this(DEFAULT_INTENT, DEFAULT_INIT_STRING);
    }

    public String threeString() {
        return builder.toString();
    }

    @Override
    public void visit(String child) {
        builder.append(currentIntent)
                .append('/')
                .append(child)
                .append('\n');
    }

    @Override
    public void beforeParent() {
        this.currentIntent = currentIntent + intent;
    }

    @Override
    public void afterParent() {
        this.currentIntent = currentIntent.substring(0, currentIntent.length() - intent.length());
    }

    @Override
    public void terminal() {
        System.out.println(this.threeString());
    }
}
