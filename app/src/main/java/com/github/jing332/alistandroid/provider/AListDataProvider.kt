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
import com.github.jing332.alistandroid.model.alist.AList
import com.github.jing332.alistandroid.util.FileUtils.mimeType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import java.io.File

class AListDataProvider : DocumentsProvider() {
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

    private val mScope = CoroutineScope(Dispatchers.IO + Job())

    override fun onCreate(): Boolean {

        return true
    }

    override fun shutdown() {
        super.shutdown()
        mScope.cancel()
    }

    private val basePath
        get() = AList.dataPath

    private val ctx: Context
        get() = context!!

    private fun getFile(documentId: String?): File {
        if (documentId == "/") return File(basePath)

        val docId = documentId.docToPath()
        return File(basePath + File.separator + docId)
    }

    private fun getDocumentId(fileAbsPath: String): String {
        return "/" + File.separator + fileAbsPath.removePrefix(AList.dataPath).removePrefix("/")
    }

    private fun String?.docToPath(): String {
        var docId = this ?: ""
        if (!docId.startsWith("/"))
            docId = "/$docId"
        return docId
    }

    override fun isChildDocument(parentDocumentId: String?, documentId: String?): Boolean {
//        Log.i(TAG, "isChildDocument: $parentDocumentId, $documentId")
//        val dir = getFile(parentDocumentId)
//        val filepath = getFile(documentId).absolutePath
//
//        return dir.listFiles()?.find { it.absolutePath == filepath } != null

        return documentId.docToPath().startsWith(parentDocumentId ?: "")
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
            add(Root.COLUMN_SUMMARY, "data")
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
        val isRoot = documentId == null || documentId == "/"
        val docId = documentId.docToPath()

        val cursor = MatrixCursor(projection ?: DEFAULT_DOCUMENT_PROJECTION)
        var flags = 0
        if (isRoot) {
            flags = flags or Document.FLAG_DIR_SUPPORTS_CREATE
        }
        flags = flags or
                Document.FLAG_SUPPORTS_DELETE or
                Document.FLAG_SUPPORTS_RENAME or
                Document.FLAG_SUPPORTS_WRITE

        val file = getFile(documentId)

        cursor.newRow().apply {
            add(Document.COLUMN_FLAGS, flags)
            add(Document.COLUMN_DOCUMENT_ID, docId)
            add(Document.COLUMN_MIME_TYPE, if (isRoot) Document.MIME_TYPE_DIR else file.mimeType)
            add(Document.COLUMN_DISPLAY_NAME, file.name)
            add(Document.COLUMN_LAST_MODIFIED, file.lastModified())
            add(Document.COLUMN_SIZE, file.length())
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
        val docId = parentDocumentId.docToPath()

        return MatrixCursor(projection ?: DEFAULT_DOCUMENT_PROJECTION).apply {
            File(AList.dataPath + File.separator + docId).listFiles()?.forEach {
                newRow().apply {
                    add(
                        Document.COLUMN_DOCUMENT_ID, it.name
                    )
                    add(
                        Document.COLUMN_MIME_TYPE,
                        if (it.isDirectory) Document.MIME_TYPE_DIR
                        else it.mimeType
                    )
                    add(Document.COLUMN_DISPLAY_NAME, it.name)
                    add(Document.COLUMN_LAST_MODIFIED, it.lastModified())
                    add(Document.COLUMN_SIZE, it.length())
                }
            }
        }
    }

    override fun openDocument(
        documentId: String?,
        mode: String?,
        signal: CancellationSignal?
    ): ParcelFileDescriptor {
        val file = getFile(documentId)
        Log.i(TAG, "openDocument: $documentId, $mode, ${file.absolutePath}")

        val m = ParcelFileDescriptor.parseMode(mode ?: "rw")
        return ParcelFileDescriptor.open(file, m)
    }

    override fun copyDocument(sourceDocumentId: String?, targetParentDocumentId: String?): String {
        val sourceFile = getFile(sourceDocumentId)
        val targetFile = getFile(targetParentDocumentId)
        sourceFile.copyTo(targetFile)

        return "/" + File.separator + targetFile.absolutePath.removePrefix(AList.dataPath)
    }

    override fun moveDocument(
        sourceDocumentId: String?,
        sourceParentDocumentId: String?,
        targetParentDocumentId: String?
    ): String {
        Log.i(
            TAG, "moveDocument: $sourceDocumentId, $sourceParentDocumentId, $targetParentDocumentId"
        )

        val target = getFile(targetParentDocumentId)
        getFile(sourceDocumentId).renameTo(target)

        return getDocumentId(target.absolutePath)
    }

    override fun deleteDocument(documentId: String?) {
        super.deleteDocument(documentId)
    }

    override fun removeDocument(documentId: String?, parentDocumentId: String?) {
        super.removeDocument(documentId, parentDocumentId)
    }

    override fun renameDocument(documentId: String?, displayName: String?): String {
        if (displayName == null) return ""
        val file = getFile(documentId)
        val target = File(file.parentFile, displayName)
        file.renameTo(target)

        return getDocumentId(target.absolutePath)
    }

    override fun createDocument(
        parentDocumentId: String?,
        mimeType: String?,
        displayName: String?
    ): String {
        Log.i(TAG, "createDocument: $parentDocumentId, $mimeType, $displayName")

        val parent = getFile(parentDocumentId)
        val target = File(parent, displayName ?: "new_file")
        target.createNewFile()

        return getDocumentId(target.absolutePath)
    }

}