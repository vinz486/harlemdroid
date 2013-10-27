package it.metaglio.harlemdroid;

import it.metaglio.harlemshaker.R;

import java.util.Random;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;

public class Harlem {

	private static final int DEFAULT_CONTENT_ANIM_DELAY = 14500;
	private static final int DEFAULT_ACTIONBAR_LOGO_DELAY = 2000;
	private static final int USE_RANDOM_ANIM = -1;

	private static final int[] ANIMATIONS = new int[] { R.anim.shaker_slow,
			R.anim.shaker_fast, R.anim.zoomin_fast, R.anim.zoomin_slow,
			R.anim.zoomout_fast, R.anim.zoomout_slow, R.anim.rotation,
			R.anim.swing };

	private static MediaPlayer mediaPlayer;
	private static OnGlobalLayoutListener contentLayoutListener;

	private static boolean isMusicRunning = false;
	private static boolean isShockMode = false;

	/***
	 * Shake, shake them all!
	 * 
	 * <br/>
	 * Currently this implementation requires that your Activity has the
	 * ActionBar Logo. If there isn't, it will simply stop.
	 * 
	 * <b>This method apply animations just to views</b>
	 * 
	 * @param activity
	 */
	public static void shake(Activity activity) {

		start(activity, false);
	}

	/***
	 * Shake, shake them all!
	 * 
	 * <br/>
	 * Currently this implementation requires that your Activity has the
	 * ActionBar Logo. If there isn't, it will simply stop.
	 * 
	 * <b>This method apply animations to views and viewgroups</b>
	 * 
	 * @param activity
	 */
	public static void shock(Activity activity) {

		start(activity, true);
	}

	private static void start(Activity activity, boolean shock) {

		if (isMusicRunning) {

			return;
		}

		isShockMode = shock;

		final View content = activity.getWindow().getDecorView();
		final View actionBarLogo = findActionBarLogo(activity.getWindow());

		if (actionBarLogo == null) {

			Log.i("HarlemDroid", "ActionBar logo not found... aborting");

			return;
		}

		mediaPlayer = MediaPlayer.create(activity, R.raw.harlemshake);

		mediaPlayer.setOnCompletionListener(new OnCompletionListener() {

			@SuppressLint("NewApi")
			@SuppressWarnings("deprecation")
			@Override
			public void onCompletion(MediaPlayer mp) {

				isMusicRunning = false;

				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {

					content.getViewTreeObserver().removeOnGlobalLayoutListener(
							contentLayoutListener);

				} else {

					content.getViewTreeObserver().removeGlobalOnLayoutListener(
							contentLayoutListener);
				}

				mediaPlayer.release();
				mediaPlayer = null;
			}
		});

		isMusicRunning = true;
		mediaPlayer.start();

		actionBarLogo.postDelayed(new Runnable() {

			@Override
			public void run() {

				applyAnimation(actionBarLogo, getActionBarLogoAnimation(), 100,
						true);
			}

		}, DEFAULT_ACTIONBAR_LOGO_DELAY);

		contentLayoutListener = new OnGlobalLayoutListener() {

			@Override
			public void onGlobalLayout() {

				scheduleAnimation(content);
			}
		};

		scheduleAnimation(content);
				
		content.getViewTreeObserver().addOnGlobalLayoutListener(
				contentLayoutListener);
	}

	private static void scheduleAnimation(final View content) {

		content.postDelayed(new Runnable() {

			@Override
			public void run() {

				if (isMusicRunning) {

					animateHierarchy(content, USE_RANDOM_ANIM);
				}
			}

		}, DEFAULT_CONTENT_ANIM_DELAY);
	}

	protected static void animateHierarchy(View content, int animId) {

		if (content.getId() != android.R.id.home
				&& content.getVisibility() == View.VISIBLE) {

			if (content instanceof ViewGroup) {

				ViewGroup vg = (ViewGroup) content;

				for (int i = 0; i < vg.getChildCount(); i++) {

					animateHierarchy(vg.getChildAt(i), animId);
				}

				if (isShockMode) {

					applyAnimation(content, getRandomAnimation(),
							new Random().nextInt(1000) + 1000, true);
				}

			} else {

				if (animId == USE_RANDOM_ANIM) {

					applyAnimation(content, getRandomAnimation(),
							new Random().nextInt(1000) + 1000, true);

				} else {

					applyAnimation(content, animId, 0, false);
				}
			}
		}
	}

	private static View findActionBarLogo(Window root) {

		return root.findViewById(android.R.id.home);
	}

	private static int getRandomAnimation() {

		int index = new Random().nextInt(ANIMATIONS.length);

		return ANIMATIONS[index];
	}

	private static int getActionBarLogoAnimation() {

		return R.anim.logo_animation;
	}

	private static void applyAnimation(final View v, final int animRes,
			final long startOffset, final boolean reschedule) {

		Animation animation = AnimationUtils.loadAnimation(v.getContext(),
				animRes);
		animation.setStartOffset(startOffset);

		animation.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationEnd(Animation animation) {

				if (reschedule && isMusicRunning) {

					applyAnimation(v, animRes, startOffset, reschedule);
				}
			}

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}
		});

		v.startAnimation(animation);
	}
}