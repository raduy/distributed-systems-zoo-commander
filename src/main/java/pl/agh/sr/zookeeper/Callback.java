package pl.agh.sr.zookeeper;

/**
 * @author Lukasz Raduj 2015 raduj.lukasz@gmail.com.
 */
@FunctionalInterface
public interface Callback {

    void invoke();
}
