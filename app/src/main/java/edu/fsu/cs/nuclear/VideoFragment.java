package edu.fsu.cs.nuclear;

        import android.content.SharedPreferences;
        import android.net.Uri;
        import android.os.Bundle;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.Button;
        import android.widget.MediaController;
        import android.widget.VideoView;

        import android.app.Fragment;


public class VideoFragment extends Fragment {

    Button play = null;
    Button stop = null;
    VideoView video;
    MediaController mediaController;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_video, container, false);

        video = v.findViewById(R.id.video);
        mediaController = new MediaController(getActivity());
        play = v.findViewById(R.id.playBtn);
        stop = v.findViewById(R.id.stopBtn);
        stop.setVisibility(View.INVISIBLE);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playVideo(view);
                System.out.println("check here");
            }
        });

        return v;
    }

    public void playVideo(View v) {
        SharedPreferences prefs = getActivity().getSharedPreferences("nukeprefs",0);

        String videoPath;
        Uri uri = null;
        //System.out.println("" + prefs.getInt("zone_flag", 0));

        switch(prefs.getInt("zone_flag",0)) {
            //Zone selection switch

            case 4:
                videoPath = "android.resource://edu.fsu.cs.nuclear/"+R.raw.fireball;
                uri = Uri.parse(videoPath);
                break;

            case 3:
                videoPath = "android.resource://edu.fsu.cs.nuclear/"+R.raw.radiation;
                uri = Uri.parse(videoPath);
                break;

            case 2:
                videoPath = "android.resource://edu.fsu.cs.nuclear/"+R.raw.air_blast;
                uri = Uri.parse(videoPath);
                break;

            case 1:
                videoPath = "android.resource://edu.fsu.cs.nuclear/"+R.raw.thermal_radiation;
                uri = Uri.parse(videoPath);
                break;

            case 0:
                uri = Uri.parse("android.resource://edu.fsu.cs.nuclear/"+R.raw.thermal_radiation);
                break;

        }

        if (uri != null) {
            video.setVideoURI(uri);
            video.setMediaController(mediaController);
            mediaController.setAnchorView(video);
            play.setVisibility(View.INVISIBLE);
            stop.setVisibility(View.VISIBLE);
            stop.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getFragmentManager().beginTransaction().remove(VideoFragment.this).commit();
                }
            });
            video.start();
        }
    }
}
