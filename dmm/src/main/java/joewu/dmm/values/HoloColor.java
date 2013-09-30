package joewu.dmm.values;

import joewu.dmm.Color;

/**
 * Created by joewu on 9/29/13.
 */
public class HoloColor {
    public final static int Gray = 0;
    public final static int RedLight = 10;
    public final static int YellowLight = 20;
    public final static int GreenLight = 30;
    public final static int BlueLight = 40;
    public final static int PurpleLight = 50;

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
