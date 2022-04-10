package com.shinleeholdings.coverstar.util;

import android.app.Activity;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.shinleeholdings.coverstar.R;

/**
 * 프로그레스바를 띄우는 헬퍼 클래스 
 * @author sdk
 *
 */
public class ProgressDialogHelper {
	private static Dialog progressDialog;

	public static void show(final Activity activity) {
		if (activity == null || activity.isFinishing() || activity.getWindow() == null) {
			return;
		}

		if (progressDialog != null && progressDialog.isShowing()) {
			return;
		}

		try {
			progressDialog = getProgressDialog(activity);
			progressDialog.setCancelable(false);
			progressDialog.show();
		} catch (Exception e) {
			progressDialog = null;
		}
	}

	/**
	 * 프로그레스 다이얼로그를 dismiss 시킨다.
	 */
	public static void dismiss() {
		try {
			if (progressDialog != null && progressDialog.isShowing()) {
				progressDialog.dismiss();
			}
		} catch (Exception e) {
		}
		progressDialog = null;
	}

	private static Dialog getProgressDialog(final Activity activity) {
		LayoutInflater mInflater = activity.getLayoutInflater();
		View dialogLayout = mInflater.inflate(R.layout.layout_progress, null);

		Dialog progress = new Dialog(activity, R.style.Theme_TransparentBackground);
		progress.requestWindowFeature(Window.FEATURE_NO_TITLE);

		Window window = progress.getWindow();
		window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
		progress.setCanceledOnTouchOutside(false);
		progress.setContentView(dialogLayout);

		return progress;
	}

}
