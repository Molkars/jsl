package dev.molkars.jsl;

public interface Lumber {
    static Lumber on(Class<?> aClass) {
        return new Lumber() {
            @Override
            public void log(String message) {
                Impl.impl.log(aClass, message);
            }

            @Override
            public void error(Throwable throwable, String message) {
                Impl.impl.error(aClass, throwable, message);
            }

            @Override
            public void error(String message) {
                Impl.impl.error(aClass, message);
            }
        };
    }

    class Impl {
        private static final Impl impl = new Impl();

        private Impl() {
        }

        public void log(Class<?> clazz, String message) {
            System.out.println(clazz.getSimpleName() + ": " + message);
        }

        public void error(Class<?> clazz, Throwable throwable, String message) {
            System.err.println(clazz.getSimpleName() + ": " + message);
            throwable.printStackTrace();
            System.exit(1);
        }

        public void error(Class<?> clazz, String message) {
            System.err.println(clazz.getSimpleName() + ": " + message);
            new Exception().printStackTrace();
            System.exit(1);
        }
    }

    default void log(String message) {
        Impl.impl.log(getClass(), message);
    }

    default void error(Throwable throwable, String message) {
        Impl.impl.error(getClass(), throwable, message);
    }

    default void error(String message) {
        Impl.impl.error(getClass(), message);
    }
}
