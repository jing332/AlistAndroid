package com.github.jing332.alistandroid.provider

import android.content.Context
import android.database.Cursor
import android.database.MatrixCursor
import android.os.CancellationSignal
import android.os.ParcelFileDescriptor
import android.provider.DocumentsContract.Document
import android.provider.DocumentsContract.Root
import android.provider.DocumentsProvider
import android.util.Log
import com.github.jing332.alistandroid.R
import com.github.jing332.alistandroid.model.alistclient.FileType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import java.net.URLDecoder
import java.net.URLEncoder

class AListProvider : DocumentsProvider() {
    companion object {
        const val TAG = "AListProvider"

        private val DEFAULT_ROOT_PROJECTION: Array<String> = arrayOf(
            Root.COLUMN_ROOT_ID,
            Root.COLUMN_MIME_TYPES,
            Root.COLUMN_FLAGS,
            Root.COLUMN_ICON,
            Root.COLUMN_TITLE,
            Root.COLUMN_SUMMARY,
            Root.COLUMN_DOCUMENT_ID,
            Root.COLUMN_AVAILABLE_BYTES
        )
        private val DEFAULT_DOCUMENT_PROJECTION: Array<String> = arrayOf(
            Document.COLUMN_DOCUMENT_ID,
            Document.COLUMN_MIME_TYPE,
            Document.COLUMN_DISPLAY_NAME,
            Document.COLUMN_LAST_MODIFIED,
            Document.COLUMN_FLAGS,
            Document.COLUMN_SIZE
        )

        const val DEFAULT_ROOT_ID = "0"
    }

    //    private val mClient = AListClient()
    private val mScope = CoroutineScope(Dispatchers.IO + Job())
    private val mManager by lazy { ProviderManager(ctx) }

    override fun onCreate(): Boolean {
//        mClient.username = "admin"
//        mClient.password = "admin"
        mManager.setUserInfo("admin", "admin")

        return true
    }

    override fun shutdown() {
        super.shutdown()
        mScope.cancel()
    }

    private val ctx: Context
        get() = context!!

    override fun isChildDocument(parentDocumentId: String?, documentId: String?): Boolean {
        Log.i(TAG, "isChildDocument: $parentDocumentId, $documentId")
        return  mManager.isChildDocument(parentDocumentId ?: "", documentId ?: "")

//        return documentId?.run { URLDecoder.decode(this, "UTF-8") }
//            ?.startsWith(parentDocumentId ?: "") == true;
//        return true
    }

    override fun getDocumentType(documentId: String?): String {
        Log.i(TAG, "getDocumentType: $documentId")
        return Document.MIME_TYPE_DIR
    }

    override fun queryRoots(projection: Array<out String>?): Cursor {
        Log.i(
            TAG, "queryRoots: " + projection?.joinToString(",")
        )
        val result = MatrixCursor(projection ?: DEFAULT_ROOT_PROJECTION)

        result.newRow().apply {
            add(Root.COLUMN_ROOT_ID, DEFAULT_ROOT_ID)
            add(Root.COLUMN_TITLE, ctx.getString(R.string.app_name))
            add(Root.COLUMN_SUMMARY, ctx.getString(R.string.provider_root_summary))
            add(Root.COLUMN_DOCUMENT_ID, "/")
            add(Root.COLUMN_MIME_TYPES, Document.MIME_TYPE_DIR)
            add(Root.COLUMN_ICON, R.drawable.alist_logo)
            add(
                Root.COLUMN_FLAGS,
                Root.FLAG_LOCAL_ONLY or Root.FLAG_SUPPORTS_IS_CHILD,
            )
        }

        return result
    }

    override fun queryDocument(documentId: String?, projection: Array<out String>?): Cursor {
        Log.i(TAG, "queryDocument: " + documentId + " " + projection?.joinToString(","))
        val isRoot = documentId == null
        val docId = documentId ?: "/"

        val cursor = MatrixCursor(projection ?: DEFAULT_DOCUMENT_PROJECTION)
        var flags = 0
        if (isRoot) {
            flags = flags or Document.FLAG_DIR_SUPPORTS_CREATE
        }
        flags = flags or
                Document.FLAG_SUPPORTS_DELETE or
                Document.FLAG_SUPPORTS_RENAME or
                Document.FLAG_SUPPORTS_WRITE

        cursor.newRow().apply {
            add(Document.COLUMN_FLAGS, flags)
            add(Document.COLUMN_DOCUMENT_ID, docId)
            add(Document.COLUMN_MIME_TYPE, Document.MIME_TYPE_DIR)
            add(Document.COLUMN_DISPLAY_NAME, "0")
//            add(Document.COLUMN_LAST_MODIFIED, "未知")
//            add(Document.COLUMN_SIZE, 0)
        }

        return cursor
    }


    override fun queryChildDocuments(
        parentDocumentId: String?,
        projection: Array<out String>?,
        sortOrder: String?
    ): Cursor {
        Log.i(
            TAG,
            "queryChildDocuments: parentId=${parentDocumentId}, projection=${
                projection?.joinToString(
                    ", "
                )
            }, sortOrder=${sortOrder}"
        )
        var docId = if (parentDocumentId == "null") "/" else parentDocumentId ?: "/"
        docId = URLDecoder.decode(docId, "UTF-8")

        return MatrixCursor(projection ?: DEFAULT_DOCUMENT_PROJECTION).apply {
            mManager.list(docId).forEach { content ->
                newRow().apply {
                    add(
                        Document.COLUMN_DOCUMENT_ID,
                        docId + URLEncoder.encode("/${content.name}", "UTF-8")
                    )
                    add(
                        Document.COLUMN_MIME_TYPE,
                        if (content.isDir) Document.MIME_TYPE_DIR
                        else FileType.values().find { it.v == content.type }?.mime ?: "*/*"
                    )
                    add(Document.COLUMN_DISPLAY_NAME, content.name)
                    add(Document.COLUMN_LAST_MODIFIED, content.modified)
                    add(Document.COLUMN_SIZE, content.size)
                }
            }
        }
    }

    override fun openDocument(
        documentId: String?,
        mode: String?,
        signal: CancellationSignal?
    ): ParcelFileDescriptor {
        return ParcelFileDescriptor.createPipe()[0].apply { close() }
    }

    /* override fun openDocument(
         documentId: String?,
         mode: String?,
         signal: CancellationSignal?
     ): ParcelFileDescriptor {
         Log.i(TAG, "openDocument: $documentId, $mode, $signal")
         val m = ParcelFileDescriptor.parseMode(mode)
         var path = documentId?.replace("//", "/") ?: throw Exception("documentId is null")
         path = URLDecoder.decode(path, "UTF-8")

         val file = runBlocking(Dispatchers.IO) {
             mManager.getFile(path)
         }

         return ParcelFileDescriptor.open(
 //            File(ctx.getExternalFilesDir("data")!!.absolutePath + "/config.json"),
             file,
             m
         )
     }*/

    override fun copyDocument(sourceDocumentId: String?, targetParentDocumentId: String?): String {
        return super.copyDocument(sourceDocumentId, targetParentDocumentId)
    }

    override fun moveDocument(
        sourceDocumentId: String?,
        sourceParentDocumentId: String?,
        targetParentDocumentId: String?
    ): String {
        Log.i(
            TAG, "moveDocument: $sourceDocumentId, $sourceParentDocumentId, $targetParentDocumentId"
        )

        return super.moveDocument(
            sourceDocumentId,
            sourceParentDocumentId,
            targetParentDocumentId
        )
    }

    override fun deleteDocument(documentId: String?) {
        super.deleteDocument(documentId)
    }

    override fun removeDocument(documentId: String?, parentDocumentId: String?) {
        super.removeDocument(documentId, parentDocumentId)
    }

    override fun renameDocument(documentId: String?, displayName: String?): String {
        val path = documentId.docToPath()

        mManager.rename(path, displayName ?: "null")
        return super.renameDocument(documentId, displayName)
    }

    private fun String?.docToPath(): String {
        return URLDecoder.decode(this, "UTF-8") ?: ""
    }

}