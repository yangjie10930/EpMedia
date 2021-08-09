package com.joe.epmediademo.Activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.joe.epmediademo.Application.MyApplication;
import com.joe.epmediademo.R;
import com.joe.epmediademo.Utils.UriUtils;

import VideoHandle.EpEditor;
import VideoHandle.EpVideo;
import VideoHandle.OnEditorListener;

public class EditActivity extends AppCompatActivity implements View.OnClickListener {

	private static final int CHOOSE_FILE = 10;
	private CheckBox cb_clip, cb_crop, cb_rotation, cb_mirror, cb_text;
	private EditText et_clip_start, et_clip_end, et_crop_x, et_crop_y, et_crop_w, et_crop_h, et_rotation, et_text_x, et_text_y, et_text;
	private TextView tv_file;
	private String videoUrl;
	private ProgressDialog mProgressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit);
		initView();
	}

	private void initView() {
		cb_clip = (CheckBox) findViewById(R.id.cb_clip);
		cb_crop = (CheckBox) findViewById(R.id.cb_crop);
		cb_rotation = (CheckBox) findViewById(R.id.cb_rotation);
		cb_mirror = (CheckBox) findViewById(R.id.cb_mirror);
		cb_text = (CheckBox) findViewById(R.id.cb_text);
		et_clip_start = (EditText) findViewById(R.id.et_clip_start);
		et_clip_end = (EditText) findViewById(R.id.et_clip_end);
		et_crop_x = (EditText) findViewById(R.id.et_crop_x);
		et_crop_y = (EditText) findViewById(R.id.et_crop_y);
		et_crop_w = (EditText) findViewById(R.id.et_crop_w);
		et_crop_h = (EditText) findViewById(R.id.et_crop_h);
		et_rotation = (EditText) findViewById(R.id.et_rotation);
		et_text_x = (EditText) findViewById(R.id.et_text_x);
		et_text_y = (EditText) findViewById(R.id.et_text_y);
		et_text = (EditText) findViewById(R.id.et_text);
		tv_file = (TextView) findViewById(R.id.tv_file);
		Button bt_file = (Button) findViewById(R.id.bt_file);
		Button bt_exec = (Button) findViewById(R.id.bt_exec);
		bt_file.setOnClickListener(this);
		bt_exec.setOnClickListener(this);
		cb_mirror.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked){
					cb_rotation.setChecked(true);
				}
			}
		});
		cb_rotation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(!isChecked){
					cb_mirror.setChecked(false);
				}
			}
		});
		mProgressDialog = new ProgressDialog(this);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		mProgressDialog.setMax(100);
		mProgressDialog.setCancelable(false);
		mProgressDialog.setCanceledOnTouchOutside(false);
		mProgressDialog.setTitle("正在处理");
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.bt_file:
				chooseFile();
				break;
			case R.id.bt_exec:
				execVideo();
//				test();
				break;
		}
	}

	/**
	 * 选择文件
	 */
	private void chooseFile() {
		Intent intent = new Intent();
		intent.setType("video/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		intent.addCategory(Intent.CATEGORY_OPENABLE);
		startActivityForResult(intent, CHOOSE_FILE);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == CHOOSE_FILE) {
			if (resultCode == RESULT_OK) {
				videoUrl = UriUtils.getPath(EditActivity.this, data.getData());
				tv_file.setText(videoUrl);
			}
		}
	}

	/**
	 * 开始编辑
	 */
	private void execVideo(){
		if(videoUrl != null && !"".equals(videoUrl)){
			EpVideo epVideo = new EpVideo(videoUrl);
			if(cb_clip.isChecked())
				epVideo.clip(Float.parseFloat(et_clip_start.getText().toString().trim()),Float.parseFloat(et_clip_end.getText().toString().trim()));
			if(cb_crop.isChecked())
				epVideo.crop(Integer.parseInt(et_crop_w.getText().toString().trim()),Integer.parseInt(et_crop_h.getText().toString().trim()),Integer.parseInt(et_crop_x.getText().toString().trim()),Integer.parseInt(et_crop_y.getText().toString().trim()));
			if(cb_rotation.isChecked())
				epVideo.rotation(Integer.parseInt(et_rotation.getText().toString().trim()),cb_mirror.isChecked());
			if(cb_text.isChecked())
				epVideo.addText(Integer.parseInt(et_text_x.getText().toString().trim()),Integer.parseInt(et_text_y.getText().toString().trim()),30,"red",MyApplication.getSavePath() + "msyh.ttf",et_text.getText().toString().trim());
			mProgressDialog.setProgress(0);
			mProgressDialog.show();
			final String outPath = MyApplication.getSavePath() + "out.mp4";
			EpEditor.exec(epVideo, new EpEditor.OutputOption(outPath), new OnEditorListener() {
				@Override
				public void onSuccess() {
					Toast.makeText(EditActivity.this, "编辑完成:"+outPath, Toast.LENGTH_SHORT).show();
					mProgressDialog.dismiss();

					Intent v = new Intent(Intent.ACTION_VIEW);
					v.setDataAndType(Uri.parse(outPath), "video/mp4");
					startActivity(v);
				}

				@Override
				public void onFailure() {
					Toast.makeText(EditActivity.this, "编辑失败", Toast.LENGTH_SHORT).show();
					mProgressDialog.dismiss();
				}

				@Override
				public void onProgress(float v) {
					mProgressDialog.setProgress((int) (v * 100));
				}
			});
		}else{
			Toast.makeText(this, "选择一个视频", Toast.LENGTH_SHORT).show();
		}
	}

	private void test(){
		final String outPath = "/storage/emulated/0/Download/music.mp4";
		EpEditor.music(videoUrl, "/storage/emulated/0/DownLoad/huluwa.aac", outPath, 1.0f, 1.0f, new OnEditorListener() {
			@Override
			public void onSuccess() {
				Toast.makeText(EditActivity.this, "编辑完成:"+outPath, Toast.LENGTH_SHORT).show();

				Intent v = new Intent(Intent.ACTION_VIEW);
				v.setDataAndType(Uri.parse(outPath), "video/mp4");
				startActivity(v);
			}

			@Override
			public void onFailure() {
				Toast.makeText(EditActivity.this, "编辑失败", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onProgress(float v) {

			}
		});
	}
}
