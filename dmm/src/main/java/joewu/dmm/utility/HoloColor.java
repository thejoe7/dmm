package joewu.dmm.utility;

import joewu.dmm.Color;
import joewu.dmm.R;

/**
 * Created by joewu on 9/29/13.
 */
public class HoloColor {
    public final static int Gray = R.color.gray;
    public final static int RedLight = R.color.ics_red;
    public final static int YellowLight = R.color.ics_yellow;
    public final static int GreenLight = R.color.ics_green;
    public final static int BlueLight = R.color.blue;
    public final static int PurpleLight = R.color.ics_purple;

    public final static int[] colors = {RedLight, YellowLight, GreenLight, BlueLight, PurpleLight};

    public static int convertFromColor(Color c) {
        switch (c) {
            case RED:
                return RedLight;
            case YELLOW:
                return YellowLight;
            case GREEN:
                return GreenLight;
            case BLUE:
                return BlueLight;
            case PURPLE:
                return PurpleLight;
            default:
                return Gray;
        }
    }
}
