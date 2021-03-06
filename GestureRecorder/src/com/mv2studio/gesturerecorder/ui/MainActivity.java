package com.mv2studio.gesturerecorder.ui;

import java.io.File;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mv2studio.gesturerecorder.R;


public class MainActivity extends Activity {

	public static final File storeFile = new File(Environment.getExternalStorageDirectory(), "gestures");
	public static final String GESTURE_TIME_MILLIS_TAG = "GESTURE_TIME",
							   SHOW_SURVEY_TAG = "SURVEY",
							   INTERNET_SWITCH = "NET_SWITCH";
	
	
	public static String[][] gestureTasks = { 
		{ "cyklus for", "FOR_", "<font color=\"#0000aa\"><b>for</b></font> (__; __; __){<br><br>}" },
		{ "cyklus while", "WHILE_", "<font color=\"#0000aa\"><b>while</b></font> (__){<br><br>}" },
		{ "cyklus do-while", "DOWHILE_", "<font color=\"#0000aa\"><b>do</b></font>{<br><br>}<font color=\"#0000aa\"><b>while</b></font>(__);"},
		{ "podmienku", "IF_", "<font color=\"#0000aa\"><b>if</b></font>(__){<br><br>}" }, 
		{ "deklaráciu premennej", "DEC_", "<font color=\"#0000aa\"><b>int</b></font> __;<br><font color=\"#0000aa\"><b>Object</b></font> __;<br>" }, 
		{ "komentár", "COM_", "<font color=\"#cccccc\">// for(int i = 0; i < j; i++){...}<br>//TODO take a break</font>" },
		{ "vymazanie riadku", "ROW_", ""},
		{ "vytvorenie novej triedy", "CLASS_", "<font color=\"#0000aa\"><b>public class </b></font>MyActivity <font color=\"#0000aa\"><b>extends </b></font>Activity{<br><br>}"},
		{ "vytvorenie novej metódy", "METHOD_", "<font color=\"#0000aa\"><b>public void </b></font>onCreate(Bundle <font color=\"#00aa00\">savedInstanceState</font>){<br><br>}"}
	};

	private ImageButton refreshButton, orderButton, settingsButton;
	private ProgressBar progBar;
	
	private Typeface tCondBold;
	private boolean menuOrderType;
	private int menuRes = R.menu.empty;
	private boolean firstLoad = true;
	private String FIRST_LOAD_TAG = "LOAD";
	
	public static boolean WORLD_EDITION = true;

	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean(FIRST_LOAD_TAG, false);
	}

	private void onCreateActionBar() {
		requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
		ActionBar bar = getActionBar();
		bar.setCustomView(R.layout.action_bar_layout);
		bar.setDisplayShowCustomEnabled(true);
		View barView = bar.getCustomView();
		
		progBar = (ProgressBar) barView.findViewById(R.id.progress_bar);
		refreshButton = (ImageButton) barView.findViewById(R.id.refresh);
		orderButton = (ImageButton) barView.findViewById(R.id.order);
		settingsButton = (ImageButton) barView.findViewById(R.id.fragment_start_settings);
		
		
		
		((TextView)barView.findViewById(R.id.action_bar_title)).setTypeface(tCondBold);
		
	}
	
	public void setOnRefreshListener(OnClickListener listener) {
		refreshButton.setOnClickListener(listener);
		Log.e("", "id: "+refreshButton);
	}
	
	public void setOnSettingsListener(OnClickListener listener) {
		settingsButton.setOnClickListener(listener);
	}
	
	public void setOnOrderChangeListener(OnClickListener listener) {
		orderButton.setOnClickListener(listener);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		tCondBold = Typeface.createFromAsset(getAssets(), "fonts/Roboto-BoldCondensed.ttf");
		
		// put progressbar to action bar 
		// requestWindowFeature MUST BE CALLED BEFORE SETTING CONTENTVIEW!
		onCreateActionBar();
		setContentView(R.layout.activity_main);
		
		// get saved variables from previous activity instance
		if (savedInstanceState != null) {
			firstLoad = savedInstanceState.getBoolean(FIRST_LOAD_TAG);
		}
		

		// set fragment and it's arguments. if it's first time activity loads
		// put fragment, otherwise let system recreate previous fragment
		if (firstLoad)
//			replaceFragment(new SurveyFragment(), false);
			replaceFragment(new StartFragment(), false);
		
		
		menuOrderType = true;
	}

	private void setAdminFrag(int type) {
		GestureReaderFragment fragment = new GestureReaderFragment();
		Bundle args = new Bundle();
		args.putInt(GestureReaderFragment.TYPE_TAG, type);
		fragment.setArguments(args);
		replaceFragment(fragment, true);
	}

	public void replaceFragment(Fragment fragment, boolean toBackStack) {
		FragmentTransaction transaction = getFragmentManager()
											.beginTransaction()
											.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
											.replace(R.id.activity_main_content, fragment);
		if (toBackStack) transaction.addToBackStack("");
		transaction.commit();
	}

	public void showProgress(boolean toShow) {
		progBar.setVisibility(toShow ? View.VISIBLE : View.GONE);
	}

	
	public static final int MENU_CLEAR = 0, MENU_GESTURE = 1, MENU_BROWSER = 2, MENU_START = 3;
	
	public void setMenu(int type) {
		refreshButton.setVisibility(View.GONE);
		orderButton.setVisibility(View.GONE);
		settingsButton.setVisibility(View.GONE);
		
		switch(type) {
			case MENU_START:
				settingsButton.setVisibility(View.VISIBLE);
				break;
			case MENU_GESTURE:
				refreshButton.setVisibility(View.VISIBLE);
				break;
			case MENU_BROWSER:
				orderButton.setVisibility(View.VISIBLE);
				if (menuOrderType) {
					orderButton.setImageResource(R.drawable.order_id);
				} else {
					orderButton.setImageResource(R.drawable.order_type);
				}

				orderButton.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View item) {
						if (menuOrderType) {
							getFragmentManager().popBackStack();
							setAdminFrag(GestureReaderFragment.SHOW_IDS);
						} else {
							getFragmentManager().popBackStack();
							setAdminFrag(GestureReaderFragment.SHOW_TYPE);
						}
						menuOrderType = !menuOrderType;
					}
				});
				break;
		}
		
	}

}
