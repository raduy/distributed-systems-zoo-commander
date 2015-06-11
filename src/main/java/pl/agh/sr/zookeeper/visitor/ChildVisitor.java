package pl.agh.sr.zookeeper.visitor;

/**
 * @author Lukasz Raduj 2015 raduj.lukasz@gmail.com.
 */
public interface ChildVisitor {

    void visit(String child);

    void beforeParent();

    void afterParent();

    void terminal();
}
