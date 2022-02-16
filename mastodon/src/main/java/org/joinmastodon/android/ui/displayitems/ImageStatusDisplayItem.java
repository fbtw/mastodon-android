package org.joinmastodon.android.ui.displayitems;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import org.joinmastodon.android.R;
import org.joinmastodon.android.fragments.BaseStatusListFragment;
import org.joinmastodon.android.model.Attachment;
import org.joinmastodon.android.model.Status;
import org.joinmastodon.android.ui.drawables.BlurhashCrossfadeDrawable;
import org.joinmastodon.android.ui.photoviewer.PhotoViewerHost;

import androidx.annotation.LayoutRes;
import me.grishka.appkit.imageloader.ImageLoaderViewHolder;
import me.grishka.appkit.imageloader.requests.ImageLoaderRequest;
import me.grishka.appkit.imageloader.requests.UrlImageLoaderRequest;

public abstract class ImageStatusDisplayItem extends StatusDisplayItem{
	public final int index;
	public final int totalPhotos;
	protected Attachment attachment;
	protected ImageLoaderRequest request;
	public final Status status;

	public ImageStatusDisplayItem(String parentID, BaseStatusListFragment parentFragment, Attachment photo, Status status, int index, int totalPhotos){
		super(parentID, parentFragment);
		this.attachment=photo;
		this.status=status;
		this.index=index;
		this.totalPhotos=totalPhotos;
	}

	@Override
	public int getImageCount(){
		return 1;
	}

	@Override
	public ImageLoaderRequest getImageRequest(int index){
		return request;
	}

	public static abstract class Holder<T extends ImageStatusDisplayItem> extends StatusDisplayItem.Holder<T> implements ImageLoaderViewHolder{
		public final ImageView photo;
		private BlurhashCrossfadeDrawable crossfadeDrawable=new BlurhashCrossfadeDrawable();
		private boolean didClear;

		public Holder(Activity activity, @LayoutRes int layout, ViewGroup parent){
			super(activity, layout, parent);
			photo=findViewById(R.id.photo);
			photo.setOnClickListener(this::onViewClick);
		}

		@Override
		public void onBind(ImageStatusDisplayItem item){
			crossfadeDrawable.setSize(item.attachment.getWidth(), item.attachment.getHeight());
			crossfadeDrawable.setBlurhashDrawable(item.attachment.blurhashPlaceholder);
			crossfadeDrawable.setCrossfadeAlpha(item.status.spoilerRevealed ? 0f : 1f);
			photo.setImageDrawable(null);
			photo.setImageDrawable(crossfadeDrawable);
			didClear=false;
		}

		@Override
		public void setImage(int index, Drawable drawable){
			crossfadeDrawable.setImageDrawable(drawable);
			if(didClear && item.status.spoilerRevealed)
				crossfadeDrawable.animateAlpha(0f);
		}

		@Override
		public void clearImage(int index){
			crossfadeDrawable.setCrossfadeAlpha(1f);
			didClear=true;
		}

		private void onViewClick(View v){
			if(!item.status.spoilerRevealed){
				item.parentFragment.onRevealSpoilerClick(this);
			}else if(item.parentFragment instanceof PhotoViewerHost){
				Status contentStatus=item.status.reblog!=null ? item.status.reblog : item.status;
				((PhotoViewerHost) item.parentFragment).openPhotoViewer(item.parentID, item.status, contentStatus.mediaAttachments.indexOf(item.attachment));
			}
		}

		public void setRevealed(boolean revealed){
			crossfadeDrawable.animateAlpha(revealed ? 0f : 1f);
		}
	}
}