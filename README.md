# multielement-dialogs
Direct to use, multiple, smooth, can be applied to Kotlin &amp; Android.
Under continuous development...

# PlateDialog
## images

## use example 1
```gradle
    PlateDialog(this).show {
        setData("川A88888")
        setOnConfirmClickListener { dialog, data ->
            Toast.makeText(this@MainActivity, data, Toast.LENGTH_SHORT).show()
        }
    }
```

## use example 2
```gradle
    PlateDialog(this).apply {
        setData("川A88888")
        setOnConfirmClickListener { dialog, data ->
            Toast.makeText(this@MainActivity, data, Toast.LENGTH_SHORT).show()
        }
    }.show()
```