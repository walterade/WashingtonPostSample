package com.example.walterademiluyi.washingtonposttest.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.Signature;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

public class HelperUtils {

	public static int getStatusBarHeight(Activity a){
	    Rect rectangle = new Rect();
	    Window window = a.getWindow();
	    window.getDecorView().getWindowVisibleDisplayFrame(rectangle);
	    return rectangle.top;
	}

	public static class SMS {
		public static String getPhoneNumber(Context c) {
			TelephonyManager tm = (TelephonyManager)c.getSystemService(Context.TELEPHONY_SERVICE);
			return tm.getLine1Number();
		}

		public static void sendSMS(String toPhoneNumber, String message) {
			SmsManager sms = SmsManager.getDefault();
			sms.sendTextMessage(toPhoneNumber, null, message, null, null);
		}
        public static byte[] getPhoneHash(String phone) {
            phone = phone.replaceAll("[^0-9]", "");
            if (phone.getBytes()[0] == '1') {
                phone = "+" + phone;
            }
            else phone = "+1" + phone;
            return Bytes.getHash(phone);
        }
	}

    public static class Bytes {
        public static byte[] getHash(String password) {
            MessageDigest digest = null;
            try {
                digest = MessageDigest.getInstance("SHA-256");
            } catch (NoSuchAlgorithmException e1) {
                e1.printStackTrace();
            }
            digest.reset();
            return digest.digest(password.getBytes());
        }

        public static String bin2hex(byte[] data) {
            return String.format("%0" + (data.length * 2) + 'x', new BigInteger(1, data));
        }
    }
	
	public static class Streams {
		public static String ToString(InputStream is) {
		    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		    StringBuilder sb = new StringBuilder();

		    String line = null;
		    try {
		        while ((line = reader.readLine()) != null) {
		            sb.append(line + "\n");
		        }
		    } catch (IOException e) {
		        e.printStackTrace();
		    } finally {
		        try {
		            is.close();
                    reader.close();
		        } catch (IOException e) {
		            e.printStackTrace();
		        }
		    }
		    return sb.toString();
		}

        public static boolean ToFile(OutputStream os, String data) {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os));
            try {
                writer.write(data);
                writer.close();
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return false;
        }

        public static boolean ToFile(String filename, String data) {
            boolean ok = false;
            OutputStream os = null;

            try {
                os = new FileOutputStream(filename);
                ok = ToFile(os, data);
                os.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return ok;
        }

        public static String ToString(String filename) {
            URLConnection conn;
            InputStream is = null;
            String result = null;

            try {
                if (filename.indexOf("http") == 0) {
                    conn = new URL(filename).openConnection();
                    conn.setUseCaches(false);
                    is = conn.getInputStream();
                }
                else {
                    is = new FileInputStream(filename);
                }
                result = ToString(is);
                is.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return result;
        }
    }
	
	public static class Tasks {
		public static boolean asyncTaskCancel(AsyncTask<?, ?, ?> task, boolean cancelTask) {
			if (cancelTask) {
				if (!task.isCancelled()) {
					task.cancel(true);
				}
				return true;
			}
			return false;
		}
	}
	
	public static class Graphics {
		//using this for pre-HoneyComb devices (Gingerbread)
		public static void setAlpha(View v, float alpha) {
			if (v instanceof ViewGroup) {
				ViewGroup vg = (ViewGroup) v;
				
				for (int i = 0; i < vg.getChildCount(); i++) {
					View cv = vg.getChildAt(i);
					setAlpha(cv, alpha);
				}
			}
			
			if (v instanceof TextView) {
				//increase alpha for text to make it clearer to read
				alpha += (1f - alpha) * (.5f);
				int color = ((TextView) v).getTextColors().getDefaultColor();
				int xalpha = (int) (Color.alpha(color) * alpha);
				int red = Color.red(color);
				int green = Color.green(color);
				int blue = Color.blue(color);
				color = Color.argb(xalpha, red, green, blue);
				((TextView) v).setTextColor(color);
			}
			else if (v instanceof ViewGroup) {
				Drawable d = v.getBackground();
				if (d instanceof ColorDrawable) {
					int color = ((ColorDrawable) d).getColor();
					int xalpha = (int) (Color.alpha(color) * alpha);
					int red = Color.red(color);
					int green = Color.green(color);
					int blue = Color.blue(color);
					color = Color.argb(xalpha, red, green, blue);
					d.mutate().setAlpha(xalpha);
				}
			}
		}
	}

	static Handler handler = new Handler();

	public static class Strings {
		public static String wordCase(String word) {
			return word.substring (0,1).toUpperCase() + 
					word.substring(1).toLowerCase();
		}

		public static String plural(int count, String string) {
			if (count != 1) {
				if (string != null) {
					char c = string.charAt(string.length() - 1);
					if (c >= 'A' && c <= 'Z') {
						string += "S";
					}
					else string += "s";
				}
			}
            string = String.format(string, count);
			return string;
		}
	}

	public static class Bitmaps {
	    // Scale and keep aspect ratio 
	    static public Bitmap scaleToFitWidth(Bitmap b, int width) {
	        float factor = width / (float) b.getWidth();
	        return Bitmap.createScaledBitmap(b, width, (int) (b.getHeight() * factor), false);
	    }

	    // Scale and keep aspect ratio     
	    static public Bitmap scaleToFitHeight(Bitmap b, int height) {
	        float factor = height / (float) b.getHeight();
	        return Bitmap.createScaledBitmap(b, (int) (b.getWidth() * factor), height, false);
	    }

	    // Scale and keep aspect ratio 
	    static public Bitmap scaleToFit(Bitmap b, int width, int height, boolean filter) {
	        float factorH2W = b.getHeight() / (float) b.getWidth();
	        float w, h;
	        
	        if (factorH2W > 1) {
		        h = height / factorH2W;
		        w = width;
	        }
	        else {
		        w = width * factorH2W;
	        	h = height;
	        }
	        
	        return Bitmap.createScaledBitmap(b, (int) w, (int) h, filter);
	    }

	    static public Bitmap scaleToFill(Bitmap b, int width, int height, boolean filter) {
	        float factorH = height / (float) b.getWidth();
	        float factorW = width / (float) b.getWidth();
	        float factorToUse = (factorH > factorW) ? factorW : factorH;
	        return Bitmap.createScaledBitmap(b, (int) (b.getWidth() * factorToUse), (int) (b.getHeight() * factorToUse), filter);
	    }
	    
	    // Scale and dont keep aspect ratio 
	    static public Bitmap strechToFill(Bitmap b, int width, int height) {
	        float factorH = height / (float) b.getHeight();
	        float factorW = width / (float) b.getWidth();
	        return Bitmap.createScaledBitmap(b, (int) (b.getWidth() * factorW), (int) (b.getHeight() * factorH), false);
	    }

	    public static Bitmap getRoundedCornerBitmap(Bitmap bitmap, float roundPx) {
	        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                    bitmap.getHeight(), Config.ARGB_8888);
	        Canvas canvas = new Canvas(output);
	     
	        final int color = 0xff424242;
	        final Paint paint = new Paint();
	        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
	        final RectF rectF = new RectF(rect);
	        //final float roundPx = 12;
	     
	        paint.setAntiAlias(true);
	        canvas.drawARGB(0, 0, 0, 0);
	        paint.setColor(color);
	        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
	     
	        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
	        canvas.drawBitmap(bitmap, rect, rect, paint);
	     
	        return output;
	    }	
	}		
	
	public static void runOnUiThread(Runnable run) {
		if (Looper.myLooper() == Looper.getMainLooper()) run.run();
		else handler.post(run);
	}
	
	public static class Timing {

		public static class Timer {
			HashMap<Runnable, MyRunnable> runs = new HashMap<Runnable, MyRunnable>();
			Runnable r;
			
			public class MyRunnable implements Runnable {
                private final boolean delayFirst;
                private boolean cancel;
				private int delay;
				private Runnable run;
				private boolean running;
				private int runCount;

				public MyRunnable(Runnable r, int delay, boolean delayFirst) {
					this.run = r;
					this.delay = delay;
                    this.delayFirst = delayFirst;
				}
				public void run() {
					if (!cancel) {
						running = true;
						if (run != null) {
                            runCount++;
                            if (!delayFirst || runCount != 1) {
                                run.run();
                            }
						}
						handler.postDelayed(this, delay);
					}
				}
				public void start() {
					if (!running) {
						runCount = 0;
						cancel = false;
						run();
					}
				}
				public void stop() {
					running = false;
					cancel = true;
					handler.removeCallbacks(this);
				}
				public void dispose() {
					stop();
					run = null;
				}
			}
			
			public int getRunCount(Runnable r) {
				return runs.get(r).runCount;
			}
			
			public void stop(Runnable r) {
				MyRunnable run = runs.get(r);
				if (run != null)
					run.stop();
			}
			
			public void start(Runnable r, int delay) {
				MyRunnable run = runs.get(r);
				if (run == null) {
					run = new MyRunnable(r, delay, false);
					runs.put(r, run);
				}
				run.start();
			}

            public void start(Runnable r, int delay, boolean delayFirst) {
                MyRunnable run = runs.get(r);
                if (run == null) {
                    run = new MyRunnable(r, delay, delayFirst);
                    runs.put(r, run);
                }
                run.start();
            }

            public void dispose() {
				for (Runnable r : runs.keySet()) {
					MyRunnable mr = runs.get(r); 
					mr.dispose();
				}
				runs.clear();
			}

		}
		
	}
	
	public static class Storage {
		public static class TempStore {
			static HashMap<String, Object> store = new HashMap<String, Object>();
			public static void push(String key, Object object) {
				store.put(key, object);
			}
			public static Object pull(String key) {
				return store.remove(key);
			}
		}
	}

	public static class Assets {
		public static String read(Context c, String fileName) {
			try {
				InputStream is = c.getAssets().open(fileName);
				BufferedReader in = new BufferedReader(new InputStreamReader(is));
				StringBuilder buf = new StringBuilder();
				String str;

			    while ((str = in.readLine()) != null) {
			    	buf.append(str);
			    }

			    in.close();
			    return buf.toString();
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}
	}

    public static class Intents {
        public static List<LabeledIntent> getExtraIntentsFor(Context c, Intent intent, String... packageNames) {
            PackageManager pm = c.getPackageManager();
            List<ResolveInfo> resInfo = pm.queryIntentActivities(intent, 0);
            List<LabeledIntent> intentList = new ArrayList<LabeledIntent>();

            for (int i = 0; i < resInfo.size(); i++) {
                // Extract the label, append it, and repackage it in a LabeledIntent
                ResolveInfo ri = resInfo.get(i);
                String packageName = ri.activityInfo.packageName.toLowerCase();

                boolean ok = false;
                for (String pkgName : packageNames) {
                    if (packageName.contains(pkgName.toLowerCase())) {
                        ok = true;
                        break;
                    }
                }

                if (ok) {
                    intentList.add(new LabeledIntent(intent, packageName, ri.loadLabel(pm), ri.icon));
                }
            }

            // convert intentList to array
            return intentList;
        }
    }

    public static class KeyHash {
        public static String get(Context c) {
            try {
                String packageName = c.getPackageName();
                PackageInfo info = c.getPackageManager().getPackageInfo(packageName, PackageManager.GET_SIGNATURES);

                for (Signature signature : info.signatures) {
                    MessageDigest md = MessageDigest.getInstance("SHA");
                    md.update(signature.toByteArray());
                    return Base64.encodeToString(md.digest(), Base64.DEFAULT);
                }
            } catch (PackageManager.NameNotFoundException e) {
            } catch (NoSuchAlgorithmException e) {
            }
            return null;
        }
    }

    public static class Drawables {
        public static void setBackground(View view, Drawable drawable) {
            int sdk = android.os.Build.VERSION.SDK_INT;
            if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                view.setBackgroundDrawable(drawable);
            } else {
                view.setBackground(drawable);
            }
        }
    }

    public static class FragmentViewPager {
        public static Fragment findFragmentByPosition(Fragment parent, ViewPager pager, FragmentPagerAdapter adapter, int position) {
            return parent.getChildFragmentManager().findFragmentByTag(
                    "android:switcher:" + pager.getId() + ":"
                            + adapter.getItemId(position));
        }
    }

    public static class Configuration {
        static final android.content.res.Configuration mLastConfiguration = new android.content.res.Configuration();
        static int mLastDensity;

        public boolean applyNewConfig(Resources res) {
            int configChanges = mLastConfiguration.updateFrom(res.getConfiguration());
            boolean densityChanged = mLastDensity != res.getDisplayMetrics().densityDpi;
            if (densityChanged || (configChanges&(ActivityInfo.CONFIG_LOCALE
                    | ActivityInfo.CONFIG_UI_MODE| ActivityInfo.CONFIG_SCREEN_LAYOUT)) != 0) {
                mLastDensity = res.getDisplayMetrics().densityDpi;
                return true;
            }
            return false;
        }

        public static int getSoftNavigationHeight(Context c) {
            WindowManager windowManager = (WindowManager) c.getSystemService(Context.WINDOW_SERVICE);
            Display d = windowManager.getDefaultDisplay();

            DisplayMetrics realDisplayMetrics = new DisplayMetrics();
            d.getRealMetrics(realDisplayMetrics);

            int realHeight = realDisplayMetrics.heightPixels;
            int realWidth = realDisplayMetrics.widthPixels;

            DisplayMetrics displayMetrics = new DisplayMetrics();
            d.getMetrics(displayMetrics);

            int displayHeight = displayMetrics.heightPixels;
            int displayWidth = displayMetrics.widthPixels;

            return (realHeight - displayHeight);
        }

        public static boolean hasSoftNavigation(Context context) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                return !ViewConfiguration.get(context).hasPermanentMenuKey();
            }
            return false;
        }
    }

    public static class TimeStamp {
        public static String toTimeStamp(Date date) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
            dateFormat.setTimeZone(TimeZone.getTimeZone("Zulu"));
            return dateFormat.format(date);
        }
        public static Date fromTimeStamp(String timestamp) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
            dateFormat.setTimeZone(TimeZone.getTimeZone("Zulu"));
            try {
                return dateFormat.parse(timestamp);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return null;
        }

    }
}
