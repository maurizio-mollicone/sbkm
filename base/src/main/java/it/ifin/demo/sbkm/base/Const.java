package it.ifin.demo.sbkm.base;

public class Const {

    public class Source {

        private Source() {
        }

        /**
         *
         */
        public static final String STOCK = "stock";

        /**
         *
         */
        public static final String PAYMENT = "payment";

    }

    public class Topic {

        private Topic() {
        }

        /**
         *
         */
        public static final String ORDERS = "orders";

        /**
         *
         */
        public static final String PAYMENT = "payment";

        /**
         *
         */
        public static final String PAYMENT_ORDERS = "payment-orders";

        /**
         *
         */
        public static final String STOCK_ORDERS = "stock-orders";

    }

    public class Status {

        private Status() {
        }

        /**
        *
        */
        public static final String NEW = "NEW";
        /**
         *
         */
        public static final String ROLLBACK = "ROLLBACK";

        /**
         *
         */
        public static final String CONFIRMED = "CONFIRMED";

        /**
         *
         */
        public static final String REJECTED = "REJECTED";
        
        /**
         *
         */
        public static final String ACCEPT = "ACCEPT";
        
        /**
         *
         */
        public static final String REJECT = "REJECT";

    }
}
