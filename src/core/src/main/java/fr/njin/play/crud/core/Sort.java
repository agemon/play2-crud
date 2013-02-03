package fr.njin.play.crud.core;

import java.util.ArrayList;
import java.util.List;

public class Sort {

    public enum Direction {
        desc,
        asc;

        public Direction reverse() {
            if (this == Direction.asc)
                return Direction.desc;
            return Direction.asc;
        }
    }

    public static class Order {
        public String key;
        public Direction direction;

        public Order(String key, Direction direction) {
            this.key = key;
            this.direction = direction;
        }

        public static Order parse(String orderString) {
            if(orderString == null || orderString.trim().isEmpty())
                return null;

            String[] fields = orderString.split(":");
            if(fields.length >= 1) {
                try{
                    Direction direction = fields.length > 1 ? Direction.valueOf(fields[1]) : Direction.asc;
                    Order o = new Order(fields[0], direction);
                    return o;
                }catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        public void reverse() {
            direction = direction.reverse();
        }

        @Override
        public String toString() {
            return key + ':' + direction.name();
        }
    }

    private List<Order> orders;

    public Sort() {
    }

    public Sort(List<Order> orders) {
        this.orders = orders;
    }

    public static Sort parse(String ordersString) {
        if (ordersString == null || ordersString.trim().isEmpty())
            return null;

        Sort sort = new Sort(new ArrayList<Order>());
        for(String orderString : ordersString.split(",")) {
            Order o = Order.parse(orderString);
            if(o != null)
                sort.orders.add(o);
        }
        return sort;
    }

    public Sort newWithKey(String key) {
        Sort newSort = new Sort();
        List<Order> orders = new ArrayList<Order>();
        Order existing = null;
        if(this.orders != null) {
            for (Order o : this.orders) {
                if(o.key.equals(key))
                    existing = new Order(o.key, o.direction);
                else
                    orders.add(new Order(o.key, o.direction));
            }
        }
        if(existing != null) {
            existing.reverse();
            orders.add(0, existing);
        }else
            orders.add(0, new Order(key, Direction.asc));
        newSort.setOrders(orders);

        return newSort;
    }

    public Order getOrder(String key) {
        if(orders == null)
            return null;
        for (Order o : orders)
            if (o.key.equals(key))
                return o;
        return null;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    @Override
    public String toString() {
        if (orders == null)
            return null;

        StringBuilder builder = new StringBuilder();
        boolean first = true;
        for(Order o : orders) {
            if(first)
                first = false;
            else
                builder.append(",");
            builder.append(o);
        }

        return builder.toString();
    }
}
