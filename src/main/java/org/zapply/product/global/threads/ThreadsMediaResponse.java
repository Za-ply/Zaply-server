package org.zapply.product.global.threads;

import java.util.List;

public record ThreadsMediaResponse(
        List<ThreadsMedia> data,
        Paging paging
) {
    public record ThreadsMedia(
            String id,
            String media_product_type,
            String media_type,
            String permalink,
            Owner owner,
            String username,
            String text,
            String timestamp,
            String thumbnail_url,
            String shortcode,
            boolean is_quote_post,
            String alt_text
    ) {
        public record Owner(
                String id
        ) {}
    }

    public record Paging(
            Cursors cursors
    ) {
        public record Cursors(
                String before,
                String after
        ) {}
    }
}
