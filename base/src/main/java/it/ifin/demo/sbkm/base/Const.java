package it.ifin.demo.sbkm.base;

public class Const {
    public class Service {

        private Service() {
        }

        /**
         *
         */
        public static final String STOCK = "STOCK";
        /**
         *
         */
        public static final String PAYMENT = "PAYMENT";
        /**
         *
         */
        public static final String ORDER_TOPIC = "orders";
        /**
         *
         */
        public static final String PAYMENT_TOPIC = "payments";
        /**
         *
         */
        public static final String STOCK_TOPIC = "stock";

    }

    public class Topic {

        private Topic() {
        }

        /**
         *
         */
        public static final String ORDERS = "orders";
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
