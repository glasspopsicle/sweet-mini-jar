# sweet-mini-jar
Android related projects are stored here.

## Android Scene Transition Support on RecyclerView Adapters
```java
class MyRecyclerViewAdapter extends RecyclerView.Adapter<ViewHolder>
                            implements OnTriggerSceneTransitionsListener<ViewHolder> {
    
    static final int ITEM_TYPE_1 = 0, ITEM_TYPE_2 = 1;
    
    final SceneRecyclerViewAdapterDelegate<ViewHolder> mDelegate = new SceneRecyclerViewAdapterDelegate<>();
    
    @Override
    public void onViewRecycled(ViewHolder viewHolder) {
      mDelegate.onViewRecycled(viewHolder);
    }
    
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
      int layoutResId;
      final Context context = parent.getContext();
      /* Initialize ViewHolder's layout */
      if (viewType == ITEM_TYPE_1) layoutResId = R.layout.item_layout_1;
      else                         layoutResId = R.layout.item_layout_2;
      
      /* Initialize ViewHolder and Scene transition triggers */
      final ViewHolder viewHolder = new ViewHolder(LayoutInflater.from(context).inflate(layoutResId, parent, false));
      viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          /* Specify the existing ViewHolder's Scene root ViewGroup and its corresponding scene layouts */
          SceneData data = SceneData.create(viewHolder, R.id.scene_root, R.layout.scene_root2);
          mDelegate.triggerSceneTransitions(MyRecyclerViewAdapter.this, data);
        }
      });
      return viewHolder;
    }
    
    /* ... */
    
    @Override
    public void onTriggerSceneTransitions(final ViewHolder holder, SparseArray<Scene> scenes) {
      if (SceneRecyclerViewAdapterDelegate.areScenesCompatible()) { // KitKat and above
        Scene scene = scenes.get(R.layout.scene_root2);
        /* Initialize scene view using Scene's setEnterAction method */
        /* ... */
        Transition t = new AutoTransition();
        t.addListener(new TransitionListenerAdapter() {
          @Override
          public void onTransitionStart(Transition transition) {
              mDelegate.dispatchSceneChangeBeginning(holder, ITEM_TYPE_2);
          }
          @Override
          public void onTransitionEnd(Transition transition) {
            mDelegate.dispatchSceneChangeFinished(holder, ITEM_TYPE_2);
          }
        });
        /* ... */
        TransitionManager.go(scene, t);
      } else { // JellyBean and below
        /* Use animations instead */
        /* You might want to use an alternative layout for your initial ViewHolder layout
           which contains both ViewGroups so you can transition between them with animations/animators. */
      }
    }
    
    @Override
    public int getItemViewType(int position) {
        return mDelegate.getItemViewType(position);
    }
}
```
