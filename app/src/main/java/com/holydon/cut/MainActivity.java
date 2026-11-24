package com.holydon.cut;

import android.app.*;import android.os.*;import android.content.*;import android.net.Uri;import android.view.*;import android.widget.*;import androidx.activity.ComponentActivity;import androidx.activity.result.ActivityResultLauncher;import androidx.activity.result.contract.ActivityResultContracts;import androidx.media3.exoplayer.ExoPlayer;import androidx.media3.common.MediaItem;import androidx.media3.ui.PlayerView;import android.media.MediaPlayer;

public class MainActivity extends ComponentActivity {
 private ExoPlayer player; private PlayerView playerView; private Uri videoUri,audioUri; private MediaPlayer preview; private Switch watermark; private boolean resumeAfterPicker=false;
 private ActivityResultLauncher<String[]> videoPicker,audioPicker;
 @Override public void onCreate(Bundle b){super.onCreate(b);getWindow().setStatusBarColor(0xff090909);getWindow().setNavigationBarColor(0xff090909);
 videoPicker=registerForActivityResult(new ActivityResultContracts.OpenDocument(),u->{if(u!=null){videoUri=u;getContentResolver().takePersistableUriPermission(u,Intent.FLAG_GRANT_READ_URI_PERMISSION);if(player!=null)player.setMediaItem(MediaItem.fromUri(u));player.prepare();player.play();}});
 audioPicker=registerForActivityResult(new ActivityResultContracts.OpenDocument(),u->{if(u!=null){audioUri=u;getContentResolver().takePersistableUriPermission(u,Intent.FLAG_GRANT_READ_URI_PERMISSION);Toast.makeText(this,"Audio selected. Tap Preview to listen.",Toast.LENGTH_SHORT).show();}if(resumeAfterPicker&&player!=null)player.play();});
 buildUi(); }
 private void buildUi(){LinearLayout root=new LinearLayout(this);root.setOrientation(1);root.setPadding(18,18,18,18);root.setBackgroundColor(0xff090909);ScrollView sc=new ScrollView(this);sc.addView(root);setContentView(sc);
 TextView title=new TextView(this);title.setText("HOLYDON CUT");title.setTextColor(0xffffffff);title.setTextSize(26);title.setGravity(Gravity.CENTER);root.addView(title);
 playerView=new PlayerView(this);playerView.setMinimumHeight(230);root.addView(playerView,new LinearLayout.LayoutParams(-1,460));
 player=new ExoPlayer.Builder(this).build();playerView.setPlayer(player);
 addBtn(root,"IMPORT VIDEO",()->videoPicker.launch(new String[]{"video/*"}));addBtn(root,"PLAY / PAUSE",()->{if(player.isPlaying())player.pause();else player.play();});
 addBtn(root,"SOUND FINDER",()->{resumeAfterPicker=player.isPlaying();if(resumeAfterPicker)player.pause();String query="";EditText input=new EditText(this);input.setHint("Search sound / song");new AlertDialog.Builder(this).setTitle("Sound Finder").setView(input).setPositiveButton("Search",(d,w)->{String q=input.getText().toString().trim();if(!q.isEmpty()){Intent i=new Intent(Intent.ACTION_VIEW,Uri.parse("https://www.google.com/search?q="+Uri.encode(q+" audio download")));startActivity(i);}}).setNegativeButton("Cancel",null).show();});
 addBtn(root,"ADD SOUND FROM PHONE",()->{resumeAfterPicker=player.isPlaying();if(resumeAfterPicker)player.pause();audioPicker.launch(new String[]{"audio/*"});});
 addBtn(root,"PREVIEW SOUND / STOP",()->{if(preview!=null&&preview.isPlaying()){preview.stop();preview.release();preview=null;return;}if(audioUri!=null){try{preview=new MediaPlayer();preview.setDataSource(this,audioUri);preview.setOnPreparedListener(MediaPlayer::start);preview.prepareAsync();}catch(Exception e){Toast.makeText(this,e.getMessage(),Toast.LENGTH_LONG).show();}}else Toast.makeText(this,"Choose audio first",Toast.LENGTH_SHORT).show();});
 watermark=new Switch(this);watermark.setText("HOLYDON watermark (optional)");watermark.setTextColor(0xffffffff);watermark.setChecked(true);root.addView(watermark);
 addBtn(root,"EXPORT VIDEO",()->Toast.makeText(this,"Export and audio mixing are not implemented in this reconstructed starter yet.",Toast.LENGTH_LONG).show());
 }
 private void addBtn(LinearLayout root,String text,Runnable action){Button b=new Button(this);b.setText(text);root.addView(b,new LinearLayout.LayoutParams(-1,-2));b.setOnClickListener(v->action.run());}
 @Override protected void onDestroy(){if(player!=null)player.release();if(preview!=null){preview.release();preview=null;}super.onDestroy();}
}
