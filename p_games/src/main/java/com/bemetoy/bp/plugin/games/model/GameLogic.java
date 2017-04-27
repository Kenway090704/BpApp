package com.bemetoy.bp.plugin.games.model;

import android.content.Context;
import android.view.View;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.bemetoy.bp.plugin.games.R;
import com.bemetoy.bp.plugin.games.databinding.UiJoinGameSuccessBinding;
import com.bemetoy.bp.sdk.app.ApplicationContext;
import com.bemetoy.stub.model.GameAddress;
import com.bemetoy.bp.protocols.ProtocolConstants;
import com.bemetoy.bp.sdk.tools.Log;
import com.bemetoy.bp.uikit.BpDialog;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Tom on 2016/5/13.
 */
public class GameLogic {

    private static final String TAG = "Games.GameLogic";

    /**
     * Get the nearest 10 game address around the center
     *
     * @param gameAddressList
     * @param center
     * @return
     */
    public static List<GameAddress> getNearest10(List<GameAddress> gameAddressList, double center[]) {
        if(gameAddressList == null || gameAddressList.size() == 0) {
            Log.i(TAG, "game address list is null or nil");
            return null;
        }

        if(center == null || center.length != 2) {
            Log.i(TAG, "center location is invalid");
            return null;
        }

        final long startTime = System.currentTimeMillis();

        if(gameAddressList.size() <= ProtocolConstants.NEAREST_LOCATION_SIZE) {
            return gameAddressList;
        }

        LatLng centerL = new LatLng(center[0],center[1]);
        List<HashMap.SimpleEntry<Double,GameAddress>> distances = new ArrayList<>();

        for(GameAddress gameAddress : gameAddressList) {
            Double distance = DistanceUtil.getDistance(centerL,new LatLng(gameAddress.getLatitude(), gameAddress.getLongitude()));
            HashMap.SimpleEntry<Double,GameAddress> entry = new HashMap.SimpleEntry(distance, gameAddress);
            distances.add(entry);
        }

        Collections.sort(distances, new Comparator<HashMap.SimpleEntry<Double, GameAddress>>() {
            @Override
            public int compare(HashMap.SimpleEntry<Double, GameAddress> lhs, HashMap.SimpleEntry<Double, GameAddress> rhs) {
                return Double.compare(lhs.getKey(), rhs.getKey());
            }
        });

        List<GameAddress> results = new ArrayList<>(ProtocolConstants.NEAREST_LOCATION_SIZE);
        List<HashMap.SimpleEntry<Double,GameAddress>> temp = distances.subList(0,10);
        for(HashMap.SimpleEntry<Double,GameAddress> v : temp) {
            results.add(v.getValue());
        }

        Log.i(TAG, "it cost %s to sort the result", System.currentTimeMillis() - startTime);
        return results;
    }

    public static String getDistanceString(long distance) {
        final StringBuilder sb = new StringBuilder();
        if(distance >= 1000) {
            return sb.append(String.format("%.1f", distance / 1000f)).append("公里").toString();
        } else if(distance < 1000 && distance > 0) {
            return sb.append(distance).append("米").toString();
        } else if (distance == ProtocolConstants.DEFAULT_DISTANCE) {
            return ApplicationContext.getCurrentContext().getString(R.string.getting_location);
        } else {
            return ApplicationContext.getCurrentContext().getString(R.string.get_location_fail);
        }
    }


    public static String getGameImg(int gameType) {
        String res = "res://img/";
        switch (gameType) {
            case ProtocolConstants.GameType.CITY_GAME:
                return res + R.drawable.game_type_city;
            case ProtocolConstants.GameType.PROVINCE_GAME:
                return res + R.drawable.game_type_province;
            case ProtocolConstants.GameType.STATE_GAME:
                return res + R.drawable.game_type_state;
            case ProtocolConstants.GameType.THEME_GAME:
                return res + R.drawable.game_type_theme;
            case ProtocolConstants.GameType.WEEK_GAME:
                return res + R.drawable.game_type_week;

        }
        return null;
    }


    /**
     * Show the game result dialog to user.
     * @param context
     * @param isSuccess join game success or not.
     * @param content the content display in the dialog.
     */
    public static void showJoinGameResultNotice(Context context, boolean isSuccess, String content) {

        String title = context.getString(R.string.sorry);
        if(isSuccess) {
            title = context.getString(R.string.congratulation);
        }

        final BpDialog<UiJoinGameSuccessBinding> dialog = new BpDialog(context, R.layout.ui_join_game_success);
        dialog.mBinding.titleTv.setText(title);
        dialog.mBinding.contentTv.setText(content);
        dialog.mBinding.confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }


    /**
     int WEEK_GAME = 1;
     int CITY_GAME = 2;
     int PROVINCE_GAME = 3;
     int STATE_GAME = 4;
     int THEME_GAME = 5;
     int TRAINING_GAME = 20;
     int PK_GAME = 21;
     * @param type
     * @return
     */
    public static String getGameType(int type) {
        switch (type) {
            case ProtocolConstants.GameType.WEEK_GAME:
                return "周赛";
            case ProtocolConstants.GameType.CITY_GAME:
                return "城市赛";
            case ProtocolConstants.GameType.PROVINCE_GAME:
                return "省赛";
            case ProtocolConstants.GameType.THEME_GAME:
                return "主题赛";
            case ProtocolConstants.GameType.PK_GAME:
                return "PK赛";
            case ProtocolConstants.GameType.STATE_GAME:
                return "全国赛";
            default:
                return "训练赛";
        }
    }
}
