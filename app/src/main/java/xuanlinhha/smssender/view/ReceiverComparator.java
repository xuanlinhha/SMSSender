package xuanlinhha.smssender.view;

import java.util.Comparator;

/**
 * Created by xuanlinhha on 30/6/18.
 */

public class ReceiverComparator implements Comparator<Receiver> {
    @Override
    public int compare(Receiver r1, Receiver r2) {
        return r1.getNo().compareTo(r2.getNo());
    }
}
