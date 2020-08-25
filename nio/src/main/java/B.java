public class B {

    private static B inst = null;

    private B() {

    }

    public static B getInstance() {
        if (inst == null) {
            inst =  new B();
        }
        return inst;
    }
}
