package com.bemetoy.bp.plugin.car.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.bemetoy.bp.autogen.protocol.Racecar;
import com.bemetoy.bp.network.IRequest;
import com.bemetoy.bp.plugin.car.R;
import com.bemetoy.bp.plugin.car.model.NetSceneActivation;
import com.bemetoy.bp.plugin.car.model.NetSceneRename;
import com.bemetoy.bp.plugin.car.model.NetSceneSaveRefit;
import com.bemetoy.bp.protocols.ProtocolConstants;
import com.bemetoy.bp.protocols.StorageConstants;
import com.bemetoy.bp.sdk.core.ThreadPool;
import com.bemetoy.bp.sdk.tools.Log;
import com.bemetoy.bp.sdk.utils.Util;
import com.bemetoy.stub.account.AccountLogic;
import com.bemetoy.stub.account.AccountManager;
import com.bemetoy.stub.app.AppCore;
import com.bemetoy.stub.network.NetSceneResponseCallback;
import com.bemetoy.stub.util.JsonManager;
import com.nineoldandroids.animation.ObjectAnimator;
import com.unity3d.player.UnityPlayer;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class UnityPlayerActivity extends Activity {
	protected UnityPlayer mUnityPlayer; // don't change the name of this
	// variable; referenced from native code

	public final static String UnityObjectName = "GameManager";
	public final static String TAG = "Car.UnityPlayerActivity";
	private View fakeLoadingDialog;
	private ObjectAnimator mAnimatorRotate;

	// Setup activity layout
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);

		getWindow().setFormat(PixelFormat.RGBX_8888); // <--- This makes xperia play happy
		ViewGroup viewGroup = (ViewGroup) getWindow().getDecorView().getRootView();


		mUnityPlayer = new UnityPlayer(this);
		setContentView(mUnityPlayer);
		FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mUnityPlayer.getLayoutParams();
		layoutParams.height = 1;
		layoutParams.width = 1;
		mUnityPlayer.setLayoutParams(layoutParams);
		mUnityPlayer.invalidate();
		mUnityPlayer.requestLayout();

		viewGroup.setBackgroundResource(R.color.window_dialog_bg);
		fakeLoadingDialog = initFakeLoadingView(viewGroup);
		viewGroup.addView(fakeLoadingDialog);

		int userId = getIntent().getIntExtra(ProtocolConstants.IntentParams.USER_ID, -1);
		byte [] sessionKey = getIntent().getByteArrayExtra(ProtocolConstants.IntentParams.SESSION_ID);

		//set the login status
		if(userId > 0) {
			AccountManager accMgr = AppCore.getCore().getAccountManager();
			accMgr.setUserId(userId);
			accMgr.getAccountInfo().setSessionKey(sessionKey);
			accMgr.initialize();
		}
	}

	// Resume Unity
	@Override
	protected void onResume() {
		super.onResume();
		mUnityPlayer.resume();

	}

	// Pause Unity
	@Override
	protected void onPause() {
		super.onPause();
		mUnityPlayer.pause();
	}

	// Quit Unity
	@Override
	protected void onDestroy() {
		mUnityPlayer.quit();
		super.onDestroy();
	}


	private View initFakeLoadingView(ViewGroup viewGroup) {
		View view = LayoutInflater.from(this).inflate(R.layout.ui_fake_loading_view, viewGroup, false);
		return view;
	}


	// This ensures the layout will be correct.
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mUnityPlayer.configurationChanged(newConfig);
	}

	// Notify Unity of the focus change.
	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		mUnityPlayer.windowFocusChanged(hasFocus);
		if(hasFocus) {
			ImageView view = (ImageView) fakeLoadingDialog.findViewById(R.id.loading_im);
			mAnimatorRotate = ObjectAnimator.ofFloat(view, "rotation", 0.0f,359.0f);
			mAnimatorRotate.setRepeatMode(Animation.RESTART);
			mAnimatorRotate.setInterpolator(new LinearInterpolator());
			mAnimatorRotate.setRepeatCount(-1);
			mAnimatorRotate.setDuration(1200);
			mAnimatorRotate.start();
		}
	}

	// For some reason the multiple keyevent type is not supported by the ndk.
	// Force event injection by overriding dispatchKeyEvent().
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		if (event.getAction() == KeyEvent.ACTION_MULTIPLE)
			return mUnityPlayer.injectEvent(event);
		return super.dispatchKeyEvent(event);
	}

	// Pass any events not handled by (unfocused) views straight to UnityPlayer
	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		return mUnityPlayer.injectEvent(event);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode == KeyEvent.KEYCODE_BACK) {
			Back();
		}
		return mUnityPlayer.injectEvent(event);

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return mUnityPlayer.injectEvent(event);
	}

	/* API12 */public boolean onGenericMotionEvent(MotionEvent event) {
		return mUnityPlayer.injectEvent(event);
	}

	// -----------------------------------------------------------------------------------------
	// 非Unity生成
	// -----------------------------------------------------------------------------------------


	/**
	 * 加载完成
	 *
	 * @param data {"type":1}
	 *             <p/>
	 *             type=1:U3D引擎加载完成
	 *             type=2:游戏资源加载完成
	 */
	public void LoadCompleted(final String data) {
		Log.i(TAG, "Android::LoadCompleted=>" + data);
		JSONObject object = JsonManager.convertString2Json(data);
		try {
			if (object.getInt("type") == 2) {
				ThreadPool.postToMainThread(new Runnable() {
					@Override
					public void run() {
						Log.e(TAG, data);
						FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) mUnityPlayer.getLayoutParams();
						layoutParams.height = FrameLayout.LayoutParams.MATCH_PARENT;
						layoutParams.width =  FrameLayout.LayoutParams.MATCH_PARENT;
						mUnityPlayer.setLayoutParams(layoutParams);
						mUnityPlayer.invalidate();
						mUnityPlayer.requestLayout();
						mUnityPlayer.requestFocus();
						fakeLoadingDialog.setVisibility(View.GONE);
						if(mAnimatorRotate != null) {
							mAnimatorRotate.cancel();
						}
					}
				});
			}
		} catch (Exception e) {
			Log.e(TAG, e.getMessage());
		}

	}

	/**
	 * 显示 Android Toast
	 *
	 * @param content
	 *            显示内容
	 * @param isLong
	 *            是否长时显示
	 */
	public void ShowToast(final String content, final boolean isLong) {
		UnityPlayer.currentActivity.runOnUiThread(new Runnable() {
			public void run() {
				Toast.makeText(UnityPlayer.currentActivity, content,
						isLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT).show();
			}
		});
	}

	/**
	 * 返回
	 */
	public void Back() {
		Intent intent = new Intent();
		intent.setAction(ProtocolConstants.BroadCastAction.U3D_LAUNCH_FINISH);
		sendBroadcast(intent);
		android.os.Process.killProcess(android.os.Process.myPid());
	}

	/**
	 * 获取初始化信息
	 *
	 * @param data
	 *            （data为null）
	 * @return 昵称：nickname 等级称号：levelTitle 头像ID：headID 经验值：exp 最大经验值：maxExp
	 *         已激活车列表：activatedCars 车ID：carID 车名：carName 引擎ID：engineID
	 *         导向ID：frontID 电池ID：batteryID 后驱ID：backID 车壳ID：shellID
	 *
	 *         如果是原装配件，配件ID为-1
	 *
	 *         数据例子如下： { "nickname" : "阿里麻麻", "levelTitle" : "飚速三星", "headID" :
	 *         1, "exp" : 1051, "maxExp" : 2000, "activatedCars" : [{ "carID" :
	 *         1, "carName" : "天王巨星", "engineID" : 1011, "frontID" : -1,
	 *         "batteryID" : 1013, "backID" : 1014, "shellID" : -1 }, { "carID"
	 *         : 3, "carName" : "天王老子", "engineID" : 1031, "frontID" : 1032,
	 *         "batteryID" : -1, "backID" : 1034, "shellID" : -1 } ] }
	 */
	public String GetInitData(String data) {
		Intent intent = getIntent();
		String accountJson = intent.getStringExtra(ProtocolConstants.IntentParams.USER_INFO);
		Log.e(TAG, "Android::GetInitData=>" + accountJson);
		return accountJson;
	}

	/**
	 * 激活车
	 *
	 * @param data
	 *            cdKey为激活码 {"carID":1,"cdKey":"EFABE123dfs"}
	 *
	 * @return null
	 */
	public String ActiveCar(String data) {
		Log.i(TAG, "Android::ActiveCar=>" + data);
		JSONObject object = null;
		try {
			object = new JSONObject(data);
			String carId = object.getString("cdKey");
			NetSceneActivation activation = new NetSceneActivation(carId, new NetSceneResponseCallback<Racecar.ActivateCdkeyResponse>() {

				@Override
				public void onRequestFailed(IRequest request) {
					UnityPlayerActivity.ActiveCarCallback(JsonManager.createGeneralJson(ProtocolConstants.JsonValue.REQUEST_NETWORK_ERROR,
							getString(R.string.network_issue),getString(R.string.active_fail)));
				}

				@Override
				public void onLocalNetworkIssue(IRequest request) {
					onRequestFailed(request);
				}

				@Override
				public void onResponse(IRequest request, Racecar.ActivateCdkeyResponse result) {
					String dataResult = null;
					//TYPE  2 表示激活的是赛车
					if(result != null && result.getPrimaryResp().getResult() == Racecar.ErrorCode.ERROR_OK_VALUE && result.getType() == 2) {
						dataResult = JsonManager.createActivationResponse(ProtocolConstants.JsonValue.REQUEST_OK,"","",
								result.getCar().getCarBaseId(), result.getCar().getUserCarId(), result.getCount());
						List<Racecar.Car> carList = new ArrayList(AccountLogic.getAccountInfo().getCar41List());
						carList.add(result.getCar());
						Intent intent = new Intent();
						intent.setAction(ProtocolConstants.BroadCastAction.TASK_UPDATE_ACTION);
						sendBroadcast(intent);

						AccountLogic.updateAccountInfo(StorageConstants.Info_Key.CARS, carList);
					} else {
						dataResult = JsonManager.createActivationResponse(ProtocolConstants.JsonValue.REQUEST_ERROR, result.getPrimaryResp().getErrorMsg(),
								getString(R.string.sorry), "",-1, 0);
					}
					UnityPlayerActivity.ActiveCarCallback(dataResult);
				}
			});
			activation.doScene();
		} catch (JSONException e) {
			Log.e(TAG, "parse data error:%s", e.getMessage());
		} finally {

		}

		return null;
	}

	/**
	 * 改车名
	 *
	 * @param data
	 *            {"carID":1,"carName":"天王巨星"}
	 *
	 * @return null
	 */
	public String SetCarName(String data) {
		Log.i(TAG, "Android::SetCarName=>" + data);
		JSONObject dataObj = JsonManager.convertString2Json(data);
		final String carId = JsonManager.getFiled(dataObj, ProtocolConstants.JsonFiled.USER_CAR_ID);
		final String carName = JsonManager.getFiled(dataObj, ProtocolConstants.JsonFiled.CAR_NAME);
		if(Util.isNullOrNil(carId) || Util.isNull(carName)) {
			String dataResult = JsonManager.createGeneralJson(ProtocolConstants.JsonValue.REQUEST_ERROR, getString(R.string.network_issue), "");
			UnityPlayerActivity.SetCarNameCallback(dataResult);
			Log.e(TAG,"invalid car name or car id");
			return null;
		}

		NetSceneRename sceneRename = new NetSceneRename(carName, carId, new NetSceneResponseCallback<Racecar.RenameCarResponse>() {

			@Override
			public void onRequestFailed(IRequest request) {
				UnityPlayerActivity.SetCarNameCallback(JsonManager.createGeneralJson(ProtocolConstants.JsonValue.REQUEST_NETWORK_ERROR,
						getString(R.string.network_issue),getString(R.string.rename_fail)));
			}

			@Override
			public void onLocalNetworkIssue(IRequest request) {
				onRequestFailed(request);
			}

			@Override
			public void onResponse(IRequest request, Racecar.RenameCarResponse result) {
				String data = null;
				if(result != null && result.getPrimaryResp().getResult() == Racecar.ErrorCode.ERROR_OK_VALUE) {
					data = JsonManager.createGeneralJson(ProtocolConstants.JsonValue.REQUEST_OK, "","");
					List<Racecar.Car> carList = new ArrayList(AccountLogic.getAccountInfo().getCar41List());
					Iterator<Racecar.Car> carIterator = carList.iterator();
					Racecar.Car newCar = null;
					while(carIterator.hasNext()) {
						Racecar.Car car = carIterator.next();
						if(car.getUserCarId() == str2Int(carId)) {
							newCar = Racecar.Car.newBuilder().mergeFrom(car).setCustomName(carName).build();
							carIterator.remove();
							break;
						}
					}
					carList.add(newCar);
					AccountLogic.updateAccountInfo(StorageConstants.Info_Key.CARS, carList);
				} else {
					data = JsonManager.createGeneralJson(ProtocolConstants.JsonValue.REQUEST_ERROR,
							result.getPrimaryResp().getErrorMsg(), getString(R.string.rename_fail));
				}
				UnityPlayerActivity.SetCarNameCallback(data);
			}
		});
		sceneRename.doScene();
		return null;
	}

	/**
	 * 保存改装
	 *
	 * @param data
	 *            { "carID" : 1, "engineID" : 1011, "frontID" : 1012,
	 *            "batteryID" : 1013, "backID" : 1014, "shellID" : 1015 }
	 *
	 * @return null
	 */
	public String SaveConvert(String data) {
		Log.i(TAG, "Android::SaveConvert=>" + data);

		final JSONObject dataObj = JsonManager.convertString2Json(data);
		if(dataObj == null) {
			Log.e(TAG, "save car json data is invalid");
			return null;
		}

		final String carId = JsonManager.getFiled(dataObj, ProtocolConstants.JsonFiled.USER_CAR_ID);
		dataObj.remove(ProtocolConstants.JsonFiled.USER_CAR_ID);

		NetSceneSaveRefit netSceneSaveRefit = new NetSceneSaveRefit(carId, dataObj.toString(), new NetSceneResponseCallback<Racecar.SaveRefitResponse>() {

			@Override
			public void onRequestFailed(IRequest request) {
				UnityPlayerActivity.SaveConvertCallback(JsonManager.createGeneralJson(ProtocolConstants.JsonValue.REQUEST_NETWORK_ERROR,
						getString(R.string.network_issue), getString(R.string.refit_fail)));
			}

			@Override
			public void onLocalNetworkIssue(IRequest request) {
				onRequestFailed(request);
			}

			@Override
			public void onResponse(IRequest request, Racecar.SaveRefitResponse result) {
				String data =  null;
				if(result != null && result.getPrimaryResp().getResult() == Racecar.ErrorCode.ERROR_OK_VALUE) {
					data = JsonManager.createGeneralJson(ProtocolConstants.JsonValue.REQUEST_OK, "","");
					List<Racecar.Car> carList = new ArrayList(AccountLogic.getAccountInfo().getCar41List());
					Iterator<Racecar.Car> carIterator = carList.iterator();
					Racecar.Car newCar = null;
					while(carIterator.hasNext()) {
						Racecar.Car car = carIterator.next();
						if(car.getUserCarId() == str2Int(carId)) {
							newCar = Racecar.Car.newBuilder().mergeFrom(car).setPart(dataObj.toString()).build();
							carIterator.remove();
							break;
						}
					}
					carList.add(newCar);
					AccountLogic.updateAccountInfo(StorageConstants.Info_Key.CARS, carList);

				} else {
					data = JsonManager.createGeneralJson(ProtocolConstants.JsonValue.REQUEST_ERROR,
							result.getPrimaryResp().getErrorMsg(), getString(R.string.refit_fail));
				}
				UnityPlayerActivity.SaveConvertCallback(data);
			}
		});
		netSceneSaveRefit.doScene();
		return null;
	}

	private int str2Int(String str) {
		int result = -1;
		try{
			result = Integer.valueOf(str);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}


	/**
	 * 激活车回调
	 *
	 * @param data
	 *            code为操作结果：0表示成功，大于0表示失败 {"code":0,"carID":1}
	 */
	public static void ActiveCarCallback(String data) {
		Log.i(TAG, "Android::ActiveCarCallback=>" + data);
		UnityPlayer
				.UnitySendMessage(UnityObjectName, "ActiveCarCallback", data);
	}

	/**
	 * 改车名回调
	 *
	 * @param data
	 *            code为操作结果：0表示成功，大于0表示失败 {"code":0}
	 */
	public static void SetCarNameCallback(String data) {
		Log.i(TAG, "Android::SetCarNameCallback=>" + data);
		UnityPlayer.UnitySendMessage(UnityObjectName, "SetCarNameCallback",
				data);
	}

	/**
	 * 保存改装回调
	 *
	 * @param data
	 *            code为操作结果：0表示成功，大于0表示失败 {"code":0}
	 */
	public static void SaveConvertCallback(String data) {
		Log.i(TAG, "Android::SaveConvertCallback=>" + data);
		UnityPlayer.UnitySendMessage(UnityObjectName, "SaveConvertCallback",
				data);
	}

	/**
	 *
	 * @param data (data为null)
	 * @return url字符串，若返回空则关闭热更新功能(如："http://172.16.2.112:8080/AndroidAssets/")
	 */
	public String GetUpdateURL(String data){
		Log.i(TAG, "Android::GetUpdateURL=>" + data);
		//return null;
		return ProtocolConstants.U3D_UPDATE_URL;
	}
}
