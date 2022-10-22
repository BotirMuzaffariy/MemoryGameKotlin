package uz.lazydevv.memorygame.ui.game

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import by.kirich1409.viewbindingdelegate.viewBinding
import uz.lazydevv.memorygame.R
import uz.lazydevv.memorygame.databinding.FragmentGameBinding
import uz.lazydevv.memorygame.models.CardM
import java.util.*

class GameFragment : Fragment(R.layout.fragment_game) {

    private val binding by viewBinding(FragmentGameBinding::bind)

    var img1: ImageView? = null
    var img2: ImageView? = null

    private var openingImages = 0
    private var availableImagesCount = ROW_COUNT * COLUMN_COUNT

    private var isImg1 = false
    private var isImg2 = true
    private var isClose = false

    private val cardList = mutableListOf<CardM>()
    private val imageViewsList = mutableListOf<ImageView>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        makeGameUI()

        loadList()
    }

    private fun loadList() = (0 until availableImagesCount).forEach { cardList.add(CardM(it, pickImage())) }

    private fun makeGameUI() {
        val ivParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT).apply {
            weight = 1f
            leftMargin = resources.getDimension(R.dimen.images_interval).toInt()
            rightMargin = resources.getDimension(R.dimen.images_interval).toInt()
        }

        val innerLlParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT).apply {
            weight = 1f
            topMargin = resources.getDimension(R.dimen.images_interval).toInt()
            bottomMargin = resources.getDimension(R.dimen.images_interval).toInt()
        }

        var imgId = 0

        repeat((1..ROW_COUNT).count()) {
            val innerLl = LinearLayout(requireContext()).apply { orientation = LinearLayout.HORIZONTAL }

            repeat((1..COLUMN_COUNT).count()) {
                val iv = ImageView(requireContext()).apply {
                    id = imgId++
                    setImageResource(R.drawable.img_for_close)
                    setOnClickListener { setImgOnClickFunction(this) }
                    setBackgroundResource(R.drawable.bg_for_img_close)
                }

                innerLl.addView(iv, ivParams)
                imageViewsList.add(iv)
            }

            binding.gameContainer.addView(innerLl, innerLlParams)
        }
    }

    private fun setImgOnClickFunction(imageView: ImageView) {
        val animOpen = AnimationUtils.loadAnimation(requireContext(), R.anim.anim_open)
        val animClose = AnimationUtils.loadAnimation(requireContext(), R.anim.anim_close)
        val animInvisible = AnimationUtils.loadAnimation(requireContext(), R.anim.anim_invisible)

        isImg1 = !isImg1
        isImg2 = !isImg2

        imageView.startAnimation(animOpen)

        animOpen.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) = changeEnabledForAllImages(false)

            override fun onAnimationEnd(animation: Animation) {
                if (isClose) {
                    img1!!.startAnimation(animClose)
                    img2!!.startAnimation(animClose)
                } else {
                    if (isImg1) {
                        openingImages++
                        img1 = binding.gameContainer.findViewById(imageView.id)

                        isImg1 = true
                        isImg2 = false
                    } else if (isImg2) {
                        openingImages++
                        img2 = binding.gameContainer.findViewById(imageView.id)

                        isImg1 = false
                        isImg2 = true
                    }

                    imageView.setBackgroundResource(R.drawable.bg_for_img)
                    imageView.setImageResource(cardList[imageView.id].imgResId)
                }

                imageView.startAnimation(animClose)
            }

            override fun onAnimationRepeat(animation: Animation) = Unit
        })

        animClose.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
                if (isClose) {
                    img2!!.setImageResource(R.drawable.img_for_close)
                    img2!!.setBackgroundResource(R.drawable.bg_for_img_close)
                    img1!!.setImageResource(R.drawable.img_for_close)
                    img1!!.setBackgroundResource(R.drawable.bg_for_img_close)
                    isImg1 = false
                    isImg2 = true
                    isClose = false
                }

                if (openingImages == 2 && img1 != null && img2 != null && img1!!.id == img2!!.id) {
                    img2 = null
                    img1!!.setBackgroundResource(R.drawable.bg_for_img_close)
                    img1!!.setImageResource(R.drawable.img_for_close)
                }
            }

            override fun onAnimationEnd(animation: Animation) {
                changeEnabledForAllImages(true)

                if (openingImages == 2) {
                    if (img2 != null) {
                        if (cardList[img1!!.id].imgResId == cardList[img2!!.id].imgResId) {
                            img1!!.startAnimation(animInvisible)
                            img2!!.startAnimation(animInvisible)
                        } else {
                            isClose = true
                            img1!!.startAnimation(animOpen)
                            img2!!.startAnimation(animOpen)
                        }
                    }
                    openingImages = 0
                }
            }

            override fun onAnimationRepeat(animation: Animation) = Unit
        })

        animInvisible.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) = Unit

            override fun onAnimationEnd(animation: Animation) {
                img1!!.visibility = View.INVISIBLE
                img2!!.visibility = View.INVISIBLE

                availableImagesCount -= 2
            }

            override fun onAnimationRepeat(animation: Animation) = Unit
        })
    }

    private fun pickImage(): Int {
        val result = when (Random().nextInt(10)) {
            0 -> R.drawable.img_1
            1 -> R.drawable.img_2
            2 -> R.drawable.img_3
            3 -> R.drawable.img_4
            4 -> R.drawable.img_5
            5 -> R.drawable.img_6
            6 -> R.drawable.img_7
            7 -> R.drawable.img_8
            8 -> R.drawable.img_9
            9 -> R.drawable.img_10
            else -> -1
        }

        val count = cardList.count { it.imgResId == result }

        return if (count < 2) result else pickImage()
    }

    private fun changeEnabledForAllImages(clickableState: Boolean) = imageViewsList.forEach { it.isEnabled = clickableState }

    companion object {

        private const val ROW_COUNT = 5
        private const val COLUMN_COUNT = 4
    }
}