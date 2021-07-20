package com.example.myapplication;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link InboxTasker#newInstance} factory method to
 * create an instance of this fragment.
 */
public class InboxTasker extends Fragment {
    private RecyclerView recyclerView;
    private ObservableInteger othUnread;
    private BadgeDrawable b;

    private List<ChatBox> mBox = new ArrayList<>();
    private List<String> taskID = new ArrayList<>();
    private List<String> chatters = new ArrayList<>();
    private List<Integer> taskStatus = new ArrayList<>();
    private TabLayout tb;


    FirebaseUser fuser;
    DatabaseReference reference, chatRef;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public InboxTasker() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment InboxTasker.
     */
    // TODO: Rename and change types and number of parameters
    public static InboxTasker newInstance(String param1, String param2) {
        InboxTasker fragment = new InboxTasker();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public InboxTasker(TabLayout tb, BadgeDrawable b){
        this.tb = tb;
        this.b = b;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setHasOptionsMenu(true);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_inbox_tasker, container, false);

        recyclerView = view.findViewById(R.id.inbox_tasker_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        fuser = FirebaseAuth.getInstance().getCurrentUser();

        reference = FirebaseDatabase.getInstance("https://taskrabbits-1621680681859-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Chats");

        chatRef = FirebaseDatabase.getInstance("https://taskrabbits-1621680681859-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Users");

        chatRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {

            }

            @Override
            public void onChildChanged(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {
                if(chatters.contains(snapshot.getKey())){
                    readChats();
                };
            }

            @Override
            public void onChildRemoved(@NonNull @NotNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });

        //get taskID first, then check relevant chats against available taskID
        FirebaseFirestore.getInstance().collection("Tasks").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable @org.jetbrains.annotations.Nullable QuerySnapshot queryDocumentSnapshots, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException e) {
                FirebaseFirestore.getInstance().collection("Tasks").get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                                List<Integer> allStatus = new ArrayList<>();
                                taskID.clear();

                                for (DocumentSnapshot snapshot : task.getResult()) {
                                    if (snapshot.exists() &&
                                            !((HashMap)snapshot.get(snapshot.getId())).get("userId").equals(fuser.getUid())) {
                                        taskID.add(snapshot.getId());
                                        allStatus.add(Integer.parseInt((String)((HashMap)snapshot.get(snapshot.getId())).get("tag")));
                                    }
                                }

                                reference.addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                        int[] unreadsOld = new int[mBox.size()];
                                        int i = 0;
                                        for (ChatBox cb: mBox) {
                                            unreadsOld[i] = cb.getAlsoUnread();
                                            i+=1;
                                        }
                                        int[] unreads = new int[mBox.size()];
                                        List<Integer> oldStatus = new ArrayList<>();
                                        oldStatus.addAll(taskStatus);

                                        int init = mBox.size();
                                        mBox.clear();
                                        chatters.clear();
                                        taskStatus.clear();

                                        for (DataSnapshot s: snapshot.getChildren()) {
                                            Chat chat = s.getValue(Chat.class);

                                            if (chat.getSender().equals(fuser.getUid()) || chat.getReceiver().equals(fuser.getUid())) {
                                                if (taskID.contains(chat.getTaskID())) {
                                                    if (allStatus.size() >= taskID.indexOf(chat.getTaskID())){
                                                    taskStatus.add(allStatus.get(taskID.indexOf(chat.getTaskID())));}

                                                    String chatter = chat.getReceiver().equals(fuser.getUid())
                                                            ? chat.getSender()
                                                            : chat.getReceiver();
                                                    ChatBox cb = new ChatBox(chat.getTaskID(), chat.getSender(), chat.getReceiver());

                                                    if (mBox.isEmpty() || !mBox.contains(cb)) {
                                                        mBox.add(cb);
                                                    }

                                                    int k = mBox.indexOf(cb);
                                                    ChatBox now = mBox.get(k);
                                                    //mypublish, use isLast as flag
                                                    if (now.isAlsoLast() &&
                                                            ((chat.isAdmin() && chat.getReceiver().equals(fuser.getUid()))||!chat.isAdmin())) {
                                                        now.addAlsoUnread();
                                                        if(init>k) {
                                                            unreads[k] += 1;
                                                        }
                                                    } else if (chat.getAlsoLast()) {
                                                        now.setAlsoLast();
                                                        if(init>k) {
                                                            unreads[k] = 0;
                                                        }
                                                    }

                                                    if (chatters.isEmpty()||!chatters.contains(chatter)){
                                                        chatters.add(chatter);
                                                    }
                                                }
                                            }
                                        }
//                                        Toast.makeText(getContext(), "old", Toast.LENGTH_SHORT).show();
//                                        Toast.makeText(getContext(), oldStatus.toString(), Toast.LENGTH_SHORT).show();
//                                        Toast.makeText(getContext(), "new", Toast.LENGTH_SHORT).show();
//                                        Toast.makeText(getContext(), taskStatus.toString(), Toast.LENGTH_SHORT).show();
                                        if(mBox.size()!=init || (!oldStatus.equals(taskStatus))) {
                                            readChats();
                                        } else {
                                            if (!unreads.equals(unreadsOld)) {
                                                boolean refresh = false;
                                                for (int p = 0; p < unreads.length;p++) {
                                                    if (unreads[p] != unreadsOld[p]) {
                                                        refresh = true;
                                                    }
                                                }
                                                if (refresh) {
                                                    readChats();
                                                }
                                            }
                                        }
                                    }
                                    @Override
                                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        Toast.makeText(getContext(), "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void readChats(){

        UserAdapter userAdapter = new UserAdapter(getContext(), mBox, false);
        recyclerView.setAdapter(userAdapter);
        this.othUnread = userAdapter.getUnread();
        if (othUnread.isNull()) {
            tb.getTabAt(1).getOrCreateBadge().setVisible(false);
            if (b!=null && ((tb.getTabAt(0).getBadge()!=null&&tb.getTabAt(0).getBadge().isVisible())||(tb.getTabAt(1).getBadge()!=null&&tb.getTabAt(1).getBadge().isVisible()))){
                b.setVisible(true);
            } else {
                b.setVisible(false);
            }
        }
        othUnread.setOnIntegerChangeListener(new ObservableInteger.OnIntegerChangeListener() {
            @Override
            public void onIntegerChanged(int newValue) {
                if (newValue!=0 && newValue!=-1) {
                    tb.getTabAt(1).getOrCreateBadge().setVisible(true);
                    tb.getTabAt(1).getOrCreateBadge().setNumber(newValue);
                } else {
                    tb.getTabAt(1).getOrCreateBadge().setVisible(false);
                }
                if (b!=null && ((tb.getTabAt(0).getBadge()!=null&&tb.getTabAt(0).getBadge().isVisible())||(tb.getTabAt(1).getBadge()!=null&&tb.getTabAt(1).getBadge().isVisible()))){
                    b.setVisible(true);
                } else {
                    b.setVisible(false);
                }
//                Toast.makeText(getContext(), valueOf(newValue), Toast.LENGTH_SHORT).show();
            }
        });


    }

    public ObservableInteger getUnread(){
        return this.othUnread;
    }
}