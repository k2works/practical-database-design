package com.example.sms.domain.model.common;

import java.util.List;

/**
 * ページネーション結果を表すクラス.
 *
 * @param <T> 要素の型
 */
public class PageResult<T> {

    private final List<T> content;
    private final int pageNumber;
    private final int pageSize;
    private final long totalElements;
    private final int totalPages;

    public PageResult(List<T> content, int pageNumber, int pageSize, long totalElements) {
        this.content = content;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.totalElements = totalElements;
        this.totalPages = pageSize > 0 ? (int) Math.ceil((double) totalElements / pageSize) : 0;
    }

    /**
     * コンテンツを取得.
     */
    public List<T> getContent() {
        return content;
    }

    /**
     * 現在のページ番号を取得（0始まり）.
     */
    public int getPage() {
        return pageNumber;
    }

    /**
     * 1ページあたりのサイズを取得.
     */
    public int getSize() {
        return pageSize;
    }

    /**
     * 総要素数を取得.
     */
    public long getTotalElements() {
        return totalElements;
    }

    /**
     * 総ページ数を取得.
     */
    public int getTotalPages() {
        return totalPages;
    }

    /**
     * 次のページがあるかどうか.
     */
    public boolean hasNext() {
        return pageNumber < totalPages - 1;
    }

    /**
     * 前のページがあるかどうか.
     */
    public boolean hasPrevious() {
        return pageNumber > 0;
    }

    /**
     * 最初のページかどうか.
     */
    public boolean isFirst() {
        return pageNumber == 0;
    }

    /**
     * 最後のページかどうか.
     */
    public boolean isLast() {
        return pageNumber >= totalPages - 1;
    }

    /**
     * 現在のページ番号（1始まり、表示用）.
     */
    public int getNumber() {
        return pageNumber + 1;
    }

    /**
     * 空のページを作成.
     */
    public static <T> PageResult<T> empty() {
        return new PageResult<>(List.of(), 0, 10, 0);
    }
}
