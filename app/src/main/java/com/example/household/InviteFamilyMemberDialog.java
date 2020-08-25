package com.example.household;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

public class InviteFamilyMemberDialog extends AppCompatDialogFragment {

    private String groupID;

    public InviteFamilyMemberDialog(String ID) {
        groupID = ID;
    }

    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.invite_family_member, null);

        builder.setView(view)
                .setTitle("Invite family member")
                .setPositiveButton("copy to clipboard", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("groupID", groupID);
                        clipboard.setPrimaryClip(clip);
                    }
                });
        TextView groupIDField = view.findViewById(R.id.groupIDInviteFamilyMember);
        groupIDField.setText(groupID);
        return builder.create();
    }
}
