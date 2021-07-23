package com.example.myapplication;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executor;

import static java.lang.String.valueOf;


public class inbox extends Fragment{

    private RecyclerView recyclerView;
    private ObservableInteger pubUnread;
    private TabLayout tb;
    private BadgeDrawable b;

    private List<ChatBox> mBox = new ArrayList<>();
    private List<String> taskID = new ArrayList<>();
    private List<String> chatters = new ArrayList<>();
    private List<Integer> taskStatus = new ArrayList<>();
    private boolean refreshStatus = false;

    private ArrayList<String> inboxDeleted;

    FirebaseUser fuser;
    DatabaseReference reference, chatRef;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public inbox(TabLayout tb, BadgeDrawable b){
        this.tb = tb;
        this.b = b;
    }

    public inbox(){
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_inbox, container, false);

        recyclerView = view.findViewById(R.id.inbox_recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setNestedScrollingEnabled(true);



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
                           if (!(((HashMap<String, Object>)snapshot.getValue()).get("inboxDeleted") == null)){
                    inboxDeleted = (ArrayList<String>)((HashMap<String, Object>)snapshot.getValue()).get("inboxDeleted");
                } else {
                    inboxDeleted = new ArrayList<>();
                }
                //get taskID first, then check relevant chats against available taskID
                FirebaseFirestore.getInstance().collection("Tasks").addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable @org.jetbrains.annotations.Nullable QuerySnapshot queryDocumentSnapshots, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException e) {
                        FirebaseFirestore.getInstance().collection("Tasks").get()
                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                                        List<Integer> oldID = new ArrayList<>();
                                        oldID.addAll(taskStatus);
                                        taskStatus.clear();
                                        taskID.clear();
                                        for (DocumentSnapshot snapshot : task.getResult()) {
                                            if (snapshot.exists() &&
                                                    ((HashMap)snapshot.get(snapshot.getId())).get("userId").equals(fuser.getUid())) {
//                                        &&snapshot.getData().get("userID").equals(fuser.getUid())

                                                taskID.add(snapshot.getId());
                                                taskStatus.add(Integer.parseInt((String)((HashMap)snapshot.get(snapshot.getId())).get("tag")));
                                            }

                                            if (!oldID.equals(taskStatus)) {
                                                refreshStatus = true;
                                            }
                                        }

                                        reference.addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                                //ensure same number of unread
                                                int[] unreadsOld = new int[mBox.size()];
                                                int i = 0;
                                                for (ChatBox cb: mBox) {
                                                    if (cb.getLast()) {
                                                        unreadsOld[i] = cb.getUnread();
                                                    } else {
                                                        unreadsOld[i] = cb.getTotalMsg();
                                                    }
                                                    i+=1;
                                                }
                                                int[] unreads = new int[mBox.size()];

                                                List<Integer> oldStatus = taskStatus;

                                                int init = mBox.size();
                                                mBox.clear();
                                                chatters.clear();

                                                for (DataSnapshot s: snapshot.getChildren()) {
                                                    Chat chat = s.getValue(Chat.class);

                                                    if (chat!=null&&chat.getSender()!=null&&chat.getReceiver()!=null&&(chat.getSender().equals(fuser.getUid()) || chat.getReceiver().equals(fuser.getUid()))) {
                                                        if (taskID.contains(chat.getTaskID())) {

                                                                String chatter = chat.getReceiver().equals(fuser.getUid())
                                                                        ? chat.getSender()
                                                                        : chat.getReceiver();
                                                            if (!inboxDeleted.contains(chat.getTaskID()) ||
                                                                    !inboxDeleted.get(inboxDeleted.indexOf(chat.getTaskID())+1).equals(chatter)) {
                                                                ChatBox cb = new ChatBox(chat.getTaskID(), chat.getSender(), chat.getReceiver());

                                                                if (mBox.isEmpty() || !mBox.contains(cb)) {
                                                                    mBox.add(cb);

                                                                } else {
                                                                    mBox.get(mBox.indexOf(cb)).addTotal();
                                                                }

                                                                int k = mBox.indexOf(cb);
                                                                ChatBox now = mBox.get(k);
                                                                //mypublish, use isLast as flag
                                                                if (now.getLast() &&
                                                                        ((chat.isAdmin() && chat.getReceiver().equals(fuser.getUid())) || !chat.isAdmin())) {
                                                                    now.addUnread();
                                                                    if (init > k) {
                                                                        unreads[k] += 1;
                                                                    }
                                                                } else if (chat.getLast()) {
                                                                    now.setLast();
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
                                                if(mBox.size()!=init) {
                                                    System.out.println("size change");
                                                    readChats();
                                                } else {
                                                    boolean refresh = false;
                                                    for (int p = 0; p < unreads.length;p++) {
                                                        if (unreads[p] != unreadsOld[p]) {
                                                            if (mBox.get(p).getLast()) {
                                                                refresh = true;
                                                                break;
                                                            } else {
                                                                if (mBox.get(p).getTotalMsg() != unreadsOld[p]) {
                                                                    refresh = true;
                                                                    break;
                                                                }
                                                            }
                                                        }
                                                    }
                                                    if (refresh || refreshStatus) {
                                                        refreshStatus = false;
                                                        readChats();
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

        return view;
    }



    @Override
    public void onResume() {
        super.onResume();
    }

    private void readChats(){
        refreshStatus = false;

        Collections.sort(mBox, new compareUnread());
        UserAdapter userAdapter = new UserAdapter(getContext(), mBox, true);
        recyclerView.setAdapter(userAdapter);
        this.pubUnread = userAdapter.getUnread();


        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull @NotNull RecyclerView recyclerView, @NonNull @NotNull RecyclerView.ViewHolder viewHolder, @NonNull @NotNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull @NotNull RecyclerView.ViewHolder viewHolder, int direction) {
                final int position = viewHolder.getAdapterPosition();
                final ChatBox myDeleted = mBox.get(position);
                removeFromFirebase(myDeleted.getTaskID(), myDeleted.getSenderID().equals(fuser.getUid())?myDeleted.getReceiverID():myDeleted.getSenderID(), position, myDeleted, userAdapter);
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
                            .setFromStartToEndIcon(R.drawable.ic_baseline_delete_24)
                            .setFromStartToEndText("Delete")
                            .setFromStartToEndBgColor(Color.parseColor("#d7011d"))
                        .create()
                        .decorate();
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper touchHelper = new ItemTouchHelper(simpleCallback);
        touchHelper.attachToRecyclerView(recyclerView);

        if (pubUnread.isNull()) {
            tb.getTabAt(0).getOrCreateBadge().setVisible(false);
            if (b!=null && ((tb.getTabAt(0).getBadge()!=null&&tb.getTabAt(0).getBadge().isVisible())||(tb.getTabAt(1).getBadge()!=null&&tb.getTabAt(1).getBadge().isVisible()))){
                b.setVisible(true);
            } else {
                b.setVisible(false);
            }
        }
        pubUnread.setOnIntegerChangeListener(new ObservableInteger.OnIntegerChangeListener() {
            @Override
            public void onIntegerChanged(int newValue) {
                if (newValue!=0 && newValue!=-1) {
                    tb.getTabAt(0).getOrCreateBadge().setVisible(true);
                    tb.getTabAt(0).getOrCreateBadge().setNumber(newValue);
                } else {
                    tb.getTabAt(0).getOrCreateBadge().setVisible(false);
                }
                if (b!=null && ((tb.getTabAt(0).getBadge()!=null&&tb.getTabAt(0).getOrCreateBadge().isVisible())||
                        (tb.getTabAt(0).getBadge()!=null&&tb.getTabAt(1).getOrCreateBadge().isVisible()))){
                    b.setVisible(true);
                } else {
                    b.setVisible(false);
                }
            }
        });

    }

    private class compareUnread implements Comparator<ChatBox> {

        @Override
        public int compare(ChatBox o2, ChatBox o1) {
            if (o1.getUnread() > 0) {
                return 1;
            } else if (o2.getUnread() > 0){
                return  -1;
            } else {
                return 0;
            }
        }
    }

    public ObservableInteger getUnread(){
        return this.pubUnread;
    }

    private void removeFromFirebase(String taskID, String chatt, int position, ChatBox myDeleted, UserAdapter userAdapter) {
        chatRef.child(fuser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                if (((HashMap<String, Object>)task.getResult().getValue()).get("inboxDeleted") == null) {
                    ArrayList<String> deleted = new ArrayList<>();
                    deleted.add(taskID);
                    deleted.add(chatt);
                    chatRef.child(fuser.getUid()).child("inboxDeleted").setValue(deleted).addOnCompleteListener(new OnCompleteListener<Void>() {
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
                    ArrayList<String> deleted = (ArrayList<String>)((HashMap<String, Object>)task.getResult().getValue()).get("inboxDeleted");
                    if (deleted.contains(taskID) && deleted.get(deleted.indexOf(taskID)+1).equals(chatt)){
                        return;
                    }
                    deleted.add(taskID);
                    deleted.add(chatt);
                    chatRef.child(fuser.getUid()).child("inboxDeleted").setValue(deleted).addOnCompleteListener(new OnCompleteListener<Void>() {
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

    private void undoDelete(String taskID, String chatterID) {
        chatRef.child(fuser.getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<DataSnapshot> task) {
                if (((HashMap<String, Object>)task.getResult().getValue()).get("inboxDeleted") == null) {
                    return;
                } else {
                    ArrayList<String> deleted = (ArrayList<String>)((HashMap<String, Object>)task.getResult().getValue()).get("inboxDeleted");
                    int posTask;
                    if (deleted.contains(taskID) && deleted.get(deleted.indexOf(taskID)+1).equals(chatterID)){
                        posTask = deleted.indexOf(taskID);
                        deleted.remove(posTask);
                        deleted.remove(posTask);
                    }
                    chatRef.child(fuser.getUid()).child("inboxDeleted").setValue(deleted).addOnCompleteListener(new OnCompleteListener<Void>() {
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
                undoDelete(myDeleted.getTaskID(), myDeleted.getSenderID().equals(fuser.getUid())?myDeleted.getReceiverID():myDeleted.getSenderID());
            }
        }).show();
    }


}