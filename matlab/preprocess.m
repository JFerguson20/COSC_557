% Performe Kmeans Clustering on the genome-pfam data.
% Shiqi Zhong
% Nov.29 2015

% skip header and read in the data
fid = fopen('/Users/ShiqiZhong/Documents/Visualization/FinalProject/COSC_557-master/data/result/translated_helix_turn_helix_PfamA.matrix.tsv');
C = textscan(fid, ['%s' repmat('%d',1,569)], 'Delimiter', '\t', 'HeaderLines', 1);
fclose(fid);

% read in header
fid = fopen('/Users/ShiqiZhong/Documents/Visualization/FinalProject/COSC_557-master/data/result/translated_helix_turn_helix_PfamA.matrix.tsv');
tline = fgets(fid);
fclose(fid);

% combine cell data 
M = [C{[2:570]}];

% convert cell to normal matrix
N = zeros(170,569);
for i = 1:170
    for j = 1:569
        N(i,j) = M(i,j);
    end
end

% final cell data for output
Final = cell(170,570);

% set up the group number for Kmeans
numGroup = 25;

% perform Kmeans and get the indices
idx = kmeans(N,numGroup);

% re-order the rows based on groups
counter = 1;
for g = 1:numGroup
    for i = 1:170
            if(idx(i)==g)
                    Final{counter,1} = C{1,1}{i};
                for j = 2:570
                    Final{counter,j} = N(i,j-1);
                end
                counter = counter + 1;
            end
    end
end

% write the grouped data to file
out = fopen('grouped_helix_turn_helix_PfamA.matrix.tsv','w');
formatData = ['%s\t' repmat('%d\t',1,569) '\n'];

fprintf(out,tline);

[nrows,ncols] = size(Final);
for row = 1:nrows
    fprintf(out,formatData,Final{row,:});
end
fclose(out);

