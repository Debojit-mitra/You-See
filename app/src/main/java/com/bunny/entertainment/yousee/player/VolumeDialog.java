package com.bunny.entertainment.yousee.player;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.bunny.entertainment.yousee.R;

public class VolumeDialog extends AppCompatDialogFragment {

    ImageView cross;
    TextView volume_no;
    SeekBar seekBar;
    AudioManager audioManager;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.vol_dialog_item, null);
        builder.setView(view);
        requireActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);

        cross = view.findViewById(R.id.vol_close);
        volume_no = view.findViewById(R.id.vol_number);
        seekBar = view.findViewById(R.id.vol_seekbar);

        audioManager = (AudioManager) requireContext().getSystemService(Context.AUDIO_SERVICE);
        seekBar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        seekBar.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
                int mediavolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                int maxvol = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                double volPer = Math.ceil((((double) mediavolume / (double) maxvol) * (double) 100));
                String vol = volPer + "";
                volume_no.setText(vol);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return builder.create();
    }
}
