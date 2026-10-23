/**
 * Навчальна демонстрація: чому наївний equals з instanceof порушує симетрію.
 * Це лише демонстрація, у реальних класах (Ticket тощо) використано getClass().
 */
public class EqualsDemo {

    static class NaiveTicket {
        protected final String movie;

        NaiveTicket(String movie) {
            this.movie = movie;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof NaiveTicket t)) return false;
            return movie.equals(t.movie);
        }

        @Override
        public int hashCode() {
            return movie.hashCode();
        }
    }

    static class NaiveTicket3D extends NaiveTicket {
        private final boolean glasses;

        NaiveTicket3D(String movie, boolean glasses) {
            super(movie);
            this.glasses = glasses;
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof NaiveTicket3D t)) return false;
            return super.equals(o) && glasses == t.glasses;
        }

        @Override
        public int hashCode() {
            return 31 * super.hashCode() + Boolean.hashCode(glasses);
        }
    }

    public static void run() {
        NaiveTicket base = new NaiveTicket("Marvel");
        NaiveTicket3D sub = new NaiveTicket3D("Marvel", true);
        System.out.println("instanceof-підхід: base.equals(sub) = " + base.equals(sub));
        System.out.println("instanceof-підхід: sub.equals(base) = " + sub.equals(base)
                + "   <- порушена симетрія!");
    }
}
