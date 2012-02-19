package net.team10.android;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.net.Uri;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.smartnsoft.droid4me.app.SmartCommands;
import com.smartnsoft.droid4me.log.Logger;
import com.smartnsoft.droid4me.log.LoggerFactory;

/**
 * @author Willy Noel
 * @since 2012.02.19
 */

public class ReparonsParisTools
{

	  /**
	   * @author Willy Noel
	   * @since 2011.07.20
	   */
	  public static class ImagePicker
	  {

	    public static interface OnBitmapSelectedListener
	    {
	      void onSelectedBitmap(Bitmap bitmap);
	    }

	    public static final int IMAGE_PICKER_REQUEST_CODE = 0;

	    protected static final Logger log = LoggerFactory.getInstance(ImagePicker.class);

	    protected final Activity activity;

	    private int requestCode;

	    protected OnBitmapSelectedListener onBitmapSelectedListener;

	    private boolean centerAndCrop;

	    private Bitmap selectedBitmap;

	    public ImagePicker(Activity activity)
	    {
	      this.activity = activity;
	    }

	    public final Bitmap getBitmap()
	    {
	      return selectedBitmap;
	    }

	    public final ImagePicker setBitmap(Bitmap bitmap)
	    {
	      selectedBitmap = bitmap;
	      return this;
	    }

	    public void clear()
	    {
	      selectedBitmap = null;
	    }

	    public final ImagePicker pickGallery(int requestCode)
	    {
	      return pickGallery(requestCode, false);
	    }

	    public final ImagePicker pickGallery(int requestCode, boolean centerAndCrop)
	    {
	      return pickGallery(requestCode, (ImagePicker.OnBitmapSelectedListener) null, false);
	    }

	    public ImagePicker pickGallery(int requestCode, final View imageView)
	    {
	      return pickGallery(requestCode, (View) null, false);
	    }

	    public ImagePicker pickGallery(int requestCode, final View imageView, boolean centerAndCrop)
	    {
	      return pickGallery(requestCode, new ImagePicker.OnBitmapSelectedListener()
	      {

			public void onSelectedBitmap(Bitmap bitmap) 
			{
			      if (imageView != null && imageView instanceof ImageView)
		          {
		            ((ImageView) imageView).setImageBitmap(bitmap);
		          }
			}
	      }, centerAndCrop);
	    }

	    public final ImagePicker pickGallery(final int requestCode, ImagePicker.OnBitmapSelectedListener onBitmapSelectedListener)
	    {
	      return pickGallery(requestCode, onBitmapSelectedListener, false);
	    }

	    public final ImagePicker pickGallery(final int requestCode, ImagePicker.OnBitmapSelectedListener onBitmapSelectedListener, boolean centerAndCrop)
	    {
	      this.requestCode = requestCode;
	      this.onBitmapSelectedListener = onBitmapSelectedListener;
	      this.centerAndCrop = centerAndCrop;
	      pickImage(requestCode);
	      return this;
	    }

	    protected void pickImage(final int requestCode)
	    {
	      try
	      {
	        activity.startActivityForResult(new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI), requestCode);
	      }
	      catch (ActivityNotFoundException exception)
	      {
	        if (log.isErrorEnabled())
	        {
	          log.error("The image picker activity cannot be found!", exception);
	        }
	        activity.runOnUiThread(new Runnable()
	        {
				public void run() 
				{
					Toast.makeText(activity, R.string.ImagePicker_toast_cannotFoundGallery, Toast.LENGTH_LONG).show();
				}
	        });
	      }
	    }

	    public final void onActivityResult(final int requestCode, int resultCode, final Intent data)
	    {
	      if (requestCode == this.requestCode)
	      {
	        if (resultCode == Activity.RESULT_OK)
	        {
	          if (data != null)
	          {
	            // We will recycle the previous bitmap, only at the end of the process, in order to avoid runtime exceptions
	            final Bitmap previousBitmap = selectedBitmap;
	            selectedBitmap = null;

	            // We do that in a background thread, so as to relief the caller
	            SmartCommands.execute( new Runnable()
	            {
	              public void run()
	              {
	                synchronized (ImagePicker.this)
	                {
	                  final Uri bitmapData = data.getData();
	                  try
	                  {
	                    AssetFileDescriptor fileDescriptor = activity.getContentResolver().openAssetFileDescriptor(bitmapData, "r");
	                    final BitmapFactory.Options options = new BitmapFactory.Options();
	                    // We first query the picture bounds
	                    options.inJustDecodeBounds = true;
	                    decodeFileDescriptorAsBitmap(fileDescriptor, options);
	                    final int bitmapWidth = options.outWidth;
	                    final int bitmapHeight = options.outHeight;
	                    final boolean isPortrait = bitmapHeight >= bitmapWidth;
	                    final double resizedSentPictureWidth = activity.getResources().getDimension(R.dimen.resizedSentPictureWidth);
	                    final double resizedSentPictureHeight = activity.getResources().getDimension(R.dimen.resizedSentPictureHeight);

	                    // And now, we decode the bitmap
	                    final int sampleSize = Math.max(1, (int) Math.ceil(Math.min((bitmapWidth) / (isPortrait == true ? resizedSentPictureHeight
	                        : resizedSentPictureWidth), (bitmapHeight) / (isPortrait == false ? resizedSentPictureHeight : resizedSentPictureWidth))));
	                    if (log.isDebugEnabled())
	                    {
	                      log.debug("The picture measures (" + bitmapWidth + "x" + bitmapHeight + ") and will be scaled by a " + sampleSize + " factor");
	                    }
	                    options.inSampleSize = sampleSize;
	                    options.inJustDecodeBounds = false;
	                    fileDescriptor = activity.getContentResolver().openAssetFileDescriptor(bitmapData, "r");
	                    selectedBitmap = decodeFileDescriptorAsBitmap(fileDescriptor, options);

	                    if (selectedBitmap != null && centerAndCrop == true)
	                    {
	                      selectedBitmap = centerCropAndResizeBitmap(selectedBitmap);
	                    }

	                    if (selectedBitmap != null)
	                    {
	                      if (log.isDebugEnabled())
	                      {
	                        log.debug("The resized picture measures (" + selectedBitmap.getWidth() + "x" + selectedBitmap.getHeight() + ")");
	                      }
	                      if (onBitmapSelectedListener != null)
	                      {
	                        activity.runOnUiThread(new Runnable()
	                        {

								public void run() 
								{
								       onBitmapSelectedListener.onSelectedBitmap(selectedBitmap);
			                            if (previousBitmap != null)
			                            {
			                              // We free the previous bitmap
			                              if (previousBitmap.isRecycled() == false)
			                              {
			                                previousBitmap.recycle();
			                              }
			                            }
								}
	                          
	                        });
	                      }
	                    }
	                    else
	                    {
	                      activity.runOnUiThread(new Runnable()
	                      {
							public void run() 
							{
								Toast.makeText(activity, R.string.ImagePicker_toast_onlyAttachPhoto, Toast.LENGTH_LONG).show();
							}
	                      });
	                    }
	                  }
	                  catch (IOException exception)
	                  {
	                    if (log.isWarnEnabled())
	                    {
	                      log.warn("Cannot decode the bitmap located at '" + bitmapData + "'", exception);
	                    }
	                  }
	                }
	              }
	            });
	          }
	        }
	      }
	    }

	    private Bitmap centerCropAndResizeBitmap(Bitmap original)
	    {
	      final int rectSize = original.getWidth() > original.getHeight() ? original.getHeight() : original.getWidth();
	      final Bitmap croppedImage = Bitmap.createBitmap(rectSize, rectSize, Bitmap.Config.RGB_565);
	      final Canvas canvas = new Canvas(croppedImage);
	      final Rect srcRect = new Rect(0, 0, original.getWidth(), original.getHeight());
	      final Rect dstRect = new Rect(0, 0, rectSize, rectSize);
	      final int dx = (srcRect.width() - dstRect.width()) / 2;
	      final int dy = (srcRect.height() - dstRect.height()) / 2;

	      // If the srcRect is too big, use the center part of it.
	      srcRect.inset(Math.max(0, dx), Math.max(0, dy));
	      // If the dstRect is too big, use the center part of it.
	      dstRect.inset(Math.max(0, -dx), Math.max(0, -dy));
	      // Draw the cropped bitmap in the center
	      canvas.drawBitmap(original, srcRect, dstRect, null);
	      original.recycle();
	      final float scale = activity.getResources().getDimension(R.dimen.avatarLargeSize) / croppedImage.getWidth();
	      final Matrix matrix = new Matrix();
	      matrix.postScale(scale, scale);
	      final Bitmap resizedBitmap = Bitmap.createBitmap(croppedImage, 0, 0, rectSize, rectSize, matrix, false);
	      croppedImage.recycle();
	      return resizedBitmap;
	    }

	    private Bitmap decodeFileDescriptorAsBitmap(AssetFileDescriptor fileDescriptor, BitmapFactory.Options options)
	        throws IOException
	    {
	      final FileInputStream inputStream = fileDescriptor.createInputStream();
	      try
	      {
	        return BitmapFactory.decodeStream(inputStream, null, options);
	      }
	      finally
	      {
	        inputStream.close();
	        fileDescriptor.close();
	      }
	    }

	    public final ByteArrayOutputStream convertBitmapToOutputStream()
	    {
	      synchronized (this)
	      {
	        if (selectedBitmap != null)
	        {
	          final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
	          selectedBitmap.compress(CompressFormat.JPEG, 100, byteArrayOutputStream);
	          if (log.isInfoEnabled())
	          {
	            log.info("Converted the bitmap to byte array output stream, with size '" + byteArrayOutputStream.size() + "', width '" + selectedBitmap.getWidth() + "', height '" + selectedBitmap.getHeight() + "'");
	          }
	          return byteArrayOutputStream;
	        }
	        else
	        {
	          return null;
	        }
	      }
	    }
	  }

}
