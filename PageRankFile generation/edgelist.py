import networkx as nx

G = nx.read_edgelist("edgeList.txt", delimiter=' ', nodetype=str, create_using=nx.DiGraph())
pr = nx.pagerank(G, alpha=0.85, personalization=None, max_iter=30, tol=1e-06, nstart=None, weight='weight',
                 dangling=None)

# print pr
id1 = "/home/mysoreja/Downloads/LATimesHuffingtonPostData/LATimesHuffingtonPostData" \
     "/LATimesDownloadData/LATimesDownloadData/"

text_file = open("external_pageRankFile.txt", "w")

for x in pr:
    text_file.write(id1 + x + '=' + str(pr[x]) + "\n")

text_file.close()
