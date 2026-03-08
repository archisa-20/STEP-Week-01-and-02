import java.util.*;

class ParkingSpot {

    enum Status { EMPTY, OCCUPIED, DELETED }

    String licensePlate;
    long entryTime;
    Status status;

    ParkingSpot() {
        status = Status.EMPTY;
    }
}

public class ParkingLot {

    private ParkingSpot[] table;
    private int capacity;
    private int size = 0;

    private int totalProbes = 0;
    private int totalParks = 0;

    public ParkingLot(int capacity) {
        this.capacity = capacity;
        table = new ParkingSpot[capacity];

        for (int i = 0; i < capacity; i++) {
            table[i] = new ParkingSpot();
        }
    }

    // Hash function
    private int hash(String licensePlate) {
        return Math.abs(licensePlate.hashCode()) % capacity;
    }

    // Park vehicle using linear probing
    public void parkVehicle(String licensePlate) {

        int index = hash(licensePlate);
        int probes = 0;

        while (table[index].status == ParkingSpot.Status.OCCUPIED) {
            index = (index + 1) % capacity;
            probes++;
        }

        table[index].licensePlate = licensePlate;
        table[index].entryTime = System.currentTimeMillis();
        table[index].status = ParkingSpot.Status.OCCUPIED;

        size++;
        totalProbes += probes;
        totalParks++;

        System.out.println("Assigned spot #" + index + " (" + probes + " probes)");
    }

    // Exit vehicle
    public void exitVehicle(String licensePlate) {

        int index = hash(licensePlate);

        while (table[index].status != ParkingSpot.Status.EMPTY) {

            if (table[index].status == ParkingSpot.Status.OCCUPIED &&
                    table[index].licensePlate.equals(licensePlate)) {

                long duration = System.currentTimeMillis() - table[index].entryTime;
                double hours = duration / (1000.0 * 60 * 60);
                double fee = hours * 5; // $5 per hour

                table[index].status = ParkingSpot.Status.DELETED;
                size--;

                System.out.println("Spot #" + index + " freed");
                System.out.println("Duration: " + String.format("%.2f", hours) + " hours");
                System.out.println("Fee: $" + String.format("%.2f", fee));

                return;
            }

            index = (index + 1) % capacity;
        }

        System.out.println("Vehicle not found");
    }

    // Find nearest available spot
    public int findNearestSpot() {

        for (int i = 0; i < capacity; i++) {
            if (table[i].status == ParkingSpot.Status.EMPTY) {
                return i;
            }
        }

        return -1;
    }

    // Statistics
    public void getStatistics() {

        double occupancy = (size * 100.0) / capacity;
        double avgProbes = totalParks == 0 ? 0 : (double) totalProbes / totalParks;

        System.out.println("Occupancy: " + String.format("%.2f", occupancy) + "%");
        System.out.println("Average Probes: " + String.format("%.2f", avgProbes));
    }

    public static void main(String[] args) {

        ParkingLot lot = new ParkingLot(500);

        lot.parkVehicle("ABC-1234");
        lot.parkVehicle("ABC-1235");
        lot.parkVehicle("XYZ-9999");

        lot.exitVehicle("ABC-1234");

        lot.getStatistics();

        int nearest = lot.findNearestSpot();
        System.out.println("Nearest available spot: " + nearest);
    }
}