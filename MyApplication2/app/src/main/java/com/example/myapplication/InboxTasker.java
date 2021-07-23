package com.example.myapplication;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.snackbar.Snackbar;
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
import com.kevincodes.recyclerview.ItemDecorator;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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

    private ArrayList<String> inboxTaskerDeleted;

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

        chatRef.child(fuser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                if (!(((HashMap<String, Object>)snapshot.getValue()).get("inboxTaskerDeleted") == null)){
                    inboxTaskerDeleted = (ArrayList<String>)((HashMap<String, Object>)snapshot.getValue()).get("inboxTaskerDeleted");
                } else {
                    inboxTaskerDeleted = new ArrayList<>();
                }

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

                                                    if (chat!=null&&chat.getSender()!=null&&chat.getReceiver()!=null&&(chat.getSender().equals(fuser.getUid()) || chat.getReceiver().equals(fuser.getUid()))) {
                                                        if (taskID.contains(chat.getTaskID())) {
                                                            if (allStatus.size() >= taskID.indexOf(chat.getTaskID())) {
                                                                taskStatus.add(allStatus.get(taskID.indexOf(chat.getTaskID())));
                                                            }

                                                            String chatter = chat.getReceiver().equals(fuser.getUid())
                                                                    ? chat.getSender()
                                                                    : chat.getReceiver();

                                                            if (!inboxTaskerDeleted.contains(chat.getTaskID())) {

                                                                ChatBox cb = new ChatBox(chat.getTaskID(), chat.getSender(), chat.getReceiver());

                                                                if (mBox.isEmpty() || !mBox.contains(cb)) {
                                                                    mBox.add(cb);
                                                                }

                                                                int k = mBox.indexOf(cb);
                                                                ChatBox now = mBox.get(k);
                                                                //mypublish, use isLast as flag
                                                                if (now.isAlsoLast() &&
                                                                        ((chat.isAdmin() && chat.getReceiver().equals(fuser.getUid())) || !chat.isAdmin())) {
                                                                    now.addAlsoUnread();
                                                                    if (init > k) {
                                                                        unreads[k] += 1;
                                                                    }
                                                                } else if (chat.getAlsoLast()) {
                                                                    now.setAlsoLast();
                                                                    if (init > k) {
                                                                        unreads[k] = 0;
                                                                    }
                                                                }

                                                                if (chatters.isEmpty() || !chatters.contains(chatter)) {
                                                                    chatters.add(chatter);
                                                                }
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
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
        //get taskID first, then check relevant chats against available taskID

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
        Collections.sort(mBox, new compareUnread());

        UserAdapter userAdapter = new UserAdapter(getContext(), mBox, false);
        recyclerView.setAdapter(userAdapter);
        this.othUnread = userAdapter.getUnread();


        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull @NotNull RecyclerView recyclerView, @NonNull @NotNull RecyclerView.ViewHolder viewHolder, @NonNull @NotNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull @NotNull RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition();
                final ChatBox myDeleted = mBox.get(position);
                removeFromFirebase(myDeleted.getTaskID(), position, myDeleted, userAdapter);
                mBox.remove(position);
                userAdapter.notifyDataSetChanged();
            }


            @Override
            public void onChildDraw(@NonNull @NotNull Canvas c, @NonNull @NotNull RecyclerView recyclerView, @NonNull @NotNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                if (viewHolder.getAdapterPosition() == -1) return;
                new ItemDecorator.Builder(c, recyclerView, viewHolder, dX, actionState)
                        .setDefaultIconTintColor(ContextCompat.getColor(getContext(), R.color.white))
                        .setDefaultTypeFace(Typeface.SANS_SERIF)
                        .setDefaultTextSize(1, 18)
                        .setDefaultTextColor(ContextCompat.getColor(getContext(), R.color.white))
                        .setFromEndToStartIcon(R.drawable.ic_baseline_delete_24)
                        .setFromEndToStartText("Delete")
                        .setFromEndToStartBgColor(Color.parseColor("#d7011d"))
                        .create()
                        .decorate();
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper touchHelper = new ItemTouchHelper(simpleCallback);
        touchHelper.attachToRecyclerView(recyclerView);

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

    private class compareUnread implements Comparator<ChatBox> {

        @Override
        public int compare(ChatBox o2, ChatBox o1) {
            if (o1.getAlsoUnread() > 0) {
                return 1;
            } else if (o2.getAlsoUnread() > 0){
                return  -1;
            } else {
                return 0;
            }
        }
    }

    public ObservableInteger getUnread(){
        return this.othUnread;
    }

    private void removeFromFirebase(String taskID, int position, ChatBox myDeleted, UserAdapter userAdapter) {
        chatRef.child(fuser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                if (((HashMap<String, Object>)task.getResult().getValue()).get("inboxTaskerDeleted") == null) {
                    ArrayList<String> deleted = new ArrayList<>();
                    deleted.add(taskID);
                    chatRef.child(fuser.getUid()).child("inboxTaskerDeleted").setValue(deleted).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<Void> task) {
                            snackPresent(position, myDeleted, userAdapter);
//                            Toast.makeText(getContext(), "Deleted", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull @NotNull Exception e) {
                            Toast.makeText(getContext(), "Deletion failed.", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    ArrayList<String> deleted = (ArrayList<String>)((HashMap<String, Object>)task.getResult().getValue()).get("inboxTaskerDeleted");
                    if (deleted.contains(taskID)){
                        return;
                    }
                    deleted.add(taskID);
                    chatRef.child(fuser.getUid()).child("inboxTaskerDeleted").setValue(deleted).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<Void> task) {
                            snackPresent(position, myDeleted, userAdapter);
//                            Toast.makeText(getContext(), "Deleted", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull @NotNull Exception e) {
                            Toast.makeText(getContext(), "Deletion failed.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Toast.makeText(getContext(), "Cannot access database, please connect to internet.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void snackPresent(int position, ChatBox myDeleted, UserAdapter userAdapter){
        Snackbar snackbar = Snackbar.make(recyclerView, "Chat Deleted", Snackbar.LENGTH_LONG);

        final FrameLayout snackBarView = (FrameLayout) snackbar.getView();
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackBarView.getChildAt(0).getLayoutParams();
        params.setMargins(params.leftMargin,
                params.topMargin,
                params.rightMargin,
                params.bottomMargin + 100);
        snackBarView.getChildAt(0).setLayoutParams(params);
        snackbar.setAction("Undo", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBox.add(position, myDeleted);
                userAdapter.notifyItemInserted(position);
                undoDelete(myDeleted.getTaskID());
            }
        }).show();
    }

    private void undoDelete(String taskID) {
        chatRef.child(fuser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                if (((HashMap<String, Object>)task.getResult().getValue()).get("inboxTaskerDeleted") == null) {
                    return;
                } else {
                    ArrayList<String> deleted = (ArrayList<String>)((HashMap<String, Object>)task.getResult().getValue()).get("inboxTaskerDeleted");
                    int posTask;
                    if (deleted.contains(taskID)){
                        posTask = deleted.indexOf(taskID);
                        deleted.remove(posTask);
                    }
                    chatRef.child(fuser.getUid()).child("inboxTaskerDeleted").setValue(deleted).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<Void> task) {
                            Toast.makeText(getContext(), "Undo successfully", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull @NotNull Exception e) {
                            Toast.makeText(getContext(), "Undo failed.", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {
                Toast.makeText(getContext(), "Cannot access database, please connect to internet.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}